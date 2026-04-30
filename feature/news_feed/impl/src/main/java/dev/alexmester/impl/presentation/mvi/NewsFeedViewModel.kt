package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.model.FeedResult
import dev.alexmester.impl.domain.usecase.GetCachedAtTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.domain.usecase.RefreshTrendsUseCase
import dev.alexmester.models.result.AppResult
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedSideEffect
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModel(
    private val observeFeedClustersUseCase: ObserveTrendsUseCase,
    private val refreshFeedUseCase: RefreshTrendsUseCase,
    private val observeReadArticleIdsUseCase: ObserveReadArticleIdsTrendsUseCase,
    private val getLastCachedAtUseCase: GetCachedAtTrendsUseCase,
) : ViewModel() {

    private val _sideEffects = Channel<NewsFeedSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _feedResult = MutableStateFlow<FeedResult>(FeedResult.Idle)
    private val _refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val clustersFlow = observeFeedClustersUseCase()
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val state: StateFlow<NewsFeedState> = combine(
        clustersFlow,
        _feedResult,
    ) { (clusters, prefs), feedResult ->
        val country = prefs.defaultCountry
        val language = prefs.defaultLanguage
        val lastCachedAt = getLastCachedAtUseCase()

        NewsFeedReducer.reduce(
            clusters = clusters,
            feedResult = feedResult,
            country = country,
            language = language,
            lastCachedAt = lastCachedAt,
        )
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsFeedState.Loading,
        )

    val readArticleIds: StateFlow<Set<Long>> = observeReadArticleIdsUseCase()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    init {
        observeLocaleChanges()
        observeManualRefresh()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        when (intent) {
            is NewsFeedIntent.Refresh -> _refreshTrigger.tryEmit(Unit)
            is NewsFeedIntent.ArticleClick -> emitSideEffect(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
        }
    }

    // При смене locale — перезапускаем через flatMapLatest
    private fun observeLocaleChanges() {
        clustersFlow
            .map { (_, prefs) -> prefs.defaultCountry to prefs.defaultLanguage }
            .distinctUntilChanged()
            .flatMapLatest {
                _feedResult.value = FeedResult.Loading
                doRefresh()
            }
            .launchIn(viewModelScope)
    }

    // Ручной refresh
    private fun observeManualRefresh() {
        _refreshTrigger
            .flatMapLatest {
                _feedResult.value = FeedResult.Loading
                doRefresh()
            }
            .launchIn(viewModelScope)
    }

    private fun doRefresh(): Flow<Unit> = flow {
        _feedResult.value = when (val result = refreshFeedUseCase()) {
            is AppResult.Success -> FeedResult.Success
            is AppResult.Failure -> {
                emitSideEffect(NewsFeedSideEffect.ShowError(result.error))
                FeedResult.Error(result.error)
            }
        }
        emit(Unit)
    }

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}

