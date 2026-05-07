package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.model.FeedResult
import dev.alexmester.impl.domain.usecase.GetCachedAtTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveUserPreferencesTrendsUseCase
import dev.alexmester.impl.domain.usecase.RefreshTrendsUseCase
import dev.alexmester.models.result.AppResult
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedSideEffect
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModel(
    private val observeTrendsUseCase: ObserveTrendsUseCase,
    private val refreshTrendsUseCase: RefreshTrendsUseCase,
    private val observeReadArticleIdsUseCase: ObserveReadArticleIdsTrendsUseCase,
    private val observeUserPreferencesTrendsUseCase: ObserveUserPreferencesTrendsUseCase,
    private val getCachedAtTrendsUseCase: GetCachedAtTrendsUseCase,
) : ViewModel() {

    private val _sideEffects = MutableSharedFlow<NewsFeedSideEffect>(extraBufferCapacity = 1)
    val sideEffects = _sideEffects.asSharedFlow()

    private val _feedResult = MutableStateFlow<FeedResult>(FeedResult.Idle)

    private var refreshJob: Job? = null

    private val trendsDataFlow = observeTrendsUseCase()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            replay = 1
        )

    val state: StateFlow<NewsFeedState> = combine(
        trendsDataFlow,
        _feedResult,
        flow { emit(getCachedAtTrendsUseCase()) },
    ) { combineData, feedResult, lastCachedAt ->
        NewsFeedReducer.reduce(
            clusters = combineData.clusters,
            feedResult = feedResult,
            country = combineData.preferences.defaultCountry,
            language = combineData.preferences.defaultLanguage,
            lastCachedAt = lastCachedAt,
        )
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsFeedState.Loading
        )

    val readArticleIds: StateFlow<Set<Long>> =
        observeReadArticleIdsUseCase()
            .map { it.toSet() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    init {
        viewModelScope.launch {
            observeUserPreferencesTrendsUseCase()
                .map { it.defaultCountry to it.defaultLanguage }
                .distinctUntilChanged()
                .collect {
                    refresh()
                }
        }
    }

    fun handleIntent(intent: NewsFeedIntent) {
        when (intent) {
            is NewsFeedIntent.Refresh -> refresh()
            is NewsFeedIntent.ArticleClick -> _sideEffects.tryEmit(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
        }
    }

    private fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _feedResult.value = FeedResult.Loading
            when (val result = refreshTrendsUseCase()) {
                is AppResult.Success -> {
                    if (result.data == 0){
                        _sideEffects.tryEmit(NewsFeedSideEffect.ShowWarning(
                            UiText.StringResource(R.string.locale_incompatible)
                        ))
                    }
                    _feedResult.value = FeedResult.Success
                }
                is AppResult.Failure -> {
                    _sideEffects.tryEmit(NewsFeedSideEffect.ShowError(result.error))
                    _feedResult.value = FeedResult.Error(result.error)
                }
            }
        }
    }
}

