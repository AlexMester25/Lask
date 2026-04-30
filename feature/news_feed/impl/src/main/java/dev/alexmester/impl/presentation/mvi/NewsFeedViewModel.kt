package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.usecase.GetCachedAtTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.domain.usecase.RefreshTrendsUseCase
import dev.alexmester.models.locale.SupportedLocales
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.result.onFailure
import dev.alexmester.models.result.onSuccess
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer.reduce
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedSideEffect
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import dev.alexmester.newsfeed.impl.presentation.feed.contentOrNull
import dev.alexmester.newsfeed.impl.presentation.feed.isOffline
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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

    val readArticleIds: StateFlow<Set<Long>> = observeReadArticleIdsUseCase()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    init {
        observeLocaleAndBootstrap()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { NewsFeedReducer.reduce(it, intent) }
        when (intent) {
            is NewsFeedIntent.Refresh -> requestFeedRefresh()
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
                requestFeedRefreshAndObserve(country, language, clustersFlow)
            }
            .launchIn(viewModelScope)
    }

    private fun requestFeedRefreshAndObserve(
        country: String,
        language: String,
        clustersFlow: SharedFlow<Pair<List<NewsCluster>, UserPreferences>>,
    ): Flow<Unit> = flow {
        handleRefreshResult(refreshFeedUseCase(), country, language)

        clustersFlow.collect { (clusters, _) ->
            if (_state.value.isOffline) return@collect
            if (clusters.isNotEmpty()) {
                _state.update {
                    NewsFeedReducer.onClustersLoaded(
                        clusters = clusters,
                        lastCachedAt = getLastCachedAtUseCase(),
                        country = country,
                        language = language
                    )
                }
            }
        }
    }

    private fun requestFeedRefresh() {
        viewModelScope.launch {
            val country = _state.value.contentOrNull?.country ?: SupportedLocales.FALLBACK_COUNTRY
            val language = _state.value.contentOrNull?.language ?: SupportedLocales.FALLBACK_LANGUAGE
            handleRefreshResult(refreshFeedUseCase(), country, language)
        }
    }

    private fun handleRefreshResult(
        result: AppResult<Int>,
        country: String,
        language: String,
    ) {
        result.onSuccess { result ->
            if (result == 0) {
                val currentClusters = _state.value.contentOrNull?.clusters.orEmpty()
                if (currentClusters.isEmpty()) {
                    _state.update { NewsFeedReducer.onEmpty(country, language) }
                }
            }
        }.onFailure { error ->
            val currentState = _state.value
            val newState = NewsFeedReducer.onNetworkError(
                state = currentState,
                error = error,
                cachedClusters = currentState.contentOrNull?.clusters.orEmpty(),
                lastCachedAt = currentState.contentOrNull?.lastCachedAt,
            )
            _state.update { newState }
            emitSideEffect(NewsFeedSideEffect.ShowError(error))
        }
    }

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}
