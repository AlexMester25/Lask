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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModel(
    private val observeTrendsUseCase: ObserveTrendsUseCase,
    private val refreshTrendsUseCase: RefreshTrendsUseCase,
    private val observeReadArticleIdsUseCase: ObserveReadArticleIdsTrendsUseCase,
    private val getCachedAtTrendsUseCase: GetCachedAtTrendsUseCase,
) : ViewModel() {

    // -------------------- SIDE EFFECTS --------------------

    private val _sideEffects = MutableSharedFlow<NewsFeedSideEffect>(
        extraBufferCapacity = 1
    )
    val sideEffects = _sideEffects.asSharedFlow()

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        _sideEffects.tryEmit(effect)
    }

    // -------------------- TRIGGERS --------------------

    private val refreshTrigger = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // -------------------- BASE FLOWS --------------------

    private val clustersFlow = observeTrendsUseCase()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            replay = 1
        )

    private val localeFlow = clustersFlow
        .map { combineData ->
            combineData.preferences.defaultCountry to
                    combineData.preferences.defaultLanguage
        }
        .distinctUntilChanged()

    private val lastCachedAtFlow = flow {
        emit(getCachedAtTrendsUseCase())
    }

    // -------------------- REFRESH PIPELINE --------------------

    private val refreshFlow: Flow<FeedResult> = merge(
        refreshTrigger,
        localeFlow.map { Unit }
    )
        .flatMapLatest {
            flow {
                emit(FeedResult.Loading)
                val result = refreshTrendsUseCase()
                emit(
                    when (result) {
                        is AppResult.Success -> FeedResult.Success
                        is AppResult.Failure -> {
                            emitSideEffect(NewsFeedSideEffect.ShowError(result.error))
                            FeedResult.Error(result.error)
                        }
                    }
                )
            }
        }
        .onStart { emit(FeedResult.Idle) }

    val state: StateFlow<NewsFeedState> = combine(
        clustersFlow,
        refreshFlow,
        lastCachedAtFlow
    ) { combineData , feedResult, lastCachedAt ->

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

    fun handleIntent(intent: NewsFeedIntent) {
        when (intent) {
            is NewsFeedIntent.Refresh -> refreshTrigger.tryEmit(Unit)
            is NewsFeedIntent.ArticleClick -> emitSideEffect(
                NewsFeedSideEffect.NavigateToArticle(
                    intent.articleId,
                    intent.articleUrl
                )
            )
        }
    }
}

