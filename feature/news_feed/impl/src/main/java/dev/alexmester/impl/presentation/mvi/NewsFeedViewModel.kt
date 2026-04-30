package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.usecase.GetCachedAtTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.domain.usecase.RefreshTrendsUseCase
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer.reduce
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedSideEffect
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import dev.alexmester.newsfeed.impl.presentation.feed.contentOrNull
import dev.alexmester.newsfeed.impl.presentation.feed.isOffline
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModel(
    private val observeFeedClustersUseCase: ObserveTrendsUseCase,
    private val refreshFeedUseCase: RefreshTrendsUseCase,
    private val observeReadArticleIdsUseCase: ObserveReadArticleIdsTrendsUseCase,
    private val getLastCachedAtUseCase: GetCachedAtTrendsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<NewsFeedState>(NewsFeedState.Loading)
    val state: StateFlow<NewsFeedState> = _state.asStateFlow()

    private val _sideEffects = Channel<NewsFeedSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val readArticleIds: StateFlow<Set<Long>> = observeReadArticleIdsUseCase()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    init {
        observeLocaleAndBootstrap()
        observeManualRefresh()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { reduce(it, intent) }
        when (intent) {
            is NewsFeedIntent.Refresh -> _refreshTrigger.tryEmit(Unit)
            is NewsFeedIntent.ArticleClick -> emitSideEffect(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
            else -> Unit
        }
    }

    private fun observeLocaleAndBootstrap() {
        val clustersFlow = observeFeedClustersUseCase()
            .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

        clustersFlow
            .map { (_, prefs) -> prefs.defaultCountry to prefs.defaultLanguage }
            .distinctUntilChanged()
            .flatMapLatest { (country, language) ->
                _state.value = NewsFeedState.Loading
                bootstrapAndObserve(country, language, clustersFlow)
            }
            .launchIn(viewModelScope)
    }

    private fun observeManualRefresh() {
        _refreshTrigger
            .onEach {
                val content = _state.value.contentOrNull
                val country = content?.country ?: "us"
                val language = content?.language ?: "en"
                handleRefreshResult(refreshFeedUseCase(), country, language)
            }
            .launchIn(viewModelScope)
    }

    private fun bootstrapAndObserve(
        country: String,
        language: String,
        clustersFlow: SharedFlow<Pair<List<NewsCluster>, UserPreferences>>,
    ): Flow<Unit> = flow {
        coroutineScope {
            // Подписка на кэш — показывает данные сразу как они есть
            launch {
                clustersFlow.collect { (clusters, _) ->
                    if (_state.value.isOffline) return@collect
                    if (clusters.isNotEmpty()) {
                        handleIntent(
                            NewsFeedIntent.ClustersLoaded(
                                clusters = clusters,
                                country = country,
                                language = language,
                                lastCachedAt = getLastCachedAtUseCase(),
                            )
                        )
                    }
                }
            }
            // Refresh идёт параллельно — кэш уже виден пока грузится сеть
            handleRefreshResult(refreshFeedUseCase(), country, language)
        }
    }

    private fun handleRefreshResult(
        result: AppResult<Int>,
        country: String,
        language: String,
    ) {
        val content = _state.value.contentOrNull
        when (result) {
            is AppResult.Success -> {
                handleIntent(NewsFeedIntent.RefreshSuccess(result.data, country, language))
                handleIntent(NewsFeedIntent.RefreshComplete)
            }
            is AppResult.Failure -> {
                handleIntent(
                    NewsFeedIntent.RefreshFailure(
                        error = result.error,
                        cachedClusters = content?.clusters.orEmpty(),
                        lastCachedAt = content?.lastCachedAt,
                    )
                )
                emitSideEffect(NewsFeedSideEffect.ShowError(result.error))
            }
        }
    }

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}

