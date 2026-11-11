package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.impl.domain.model.FeedLoadState
import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.models.result.AppResult
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
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository,
    private val newsFeedRepository: NewsFeedRepository,
    private val observeTrendsUseCase: ObserveTrendsUseCase,
) : ViewModel() {

    private val _sideEffects = MutableSharedFlow<NewsFeedSideEffect>(extraBufferCapacity = 1)
    val sideEffects = _sideEffects.asSharedFlow()

    private val _feedLoadState = MutableStateFlow<FeedLoadState>(FeedLoadState.Idle)

    private var refreshJob: Job? = null

    private val trendsDataFlow = observeTrendsUseCase()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            replay = 1
        )

    val state: StateFlow<NewsFeedState> = combine(
        trendsDataFlow,
        _feedLoadState,
        flow { emit(newsFeedRepository.getLastCachedAt()) },
    ) { combineData, loadState, lastCachedAt ->
        NewsFeedReducer.reduce(
            clusters = combineData.clusters,
            loadState = loadState,
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
        articleRepository.observeReadArticleIds()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    init {
        observePreferencesChanges()
        refresh()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        when (intent) {
            is NewsFeedIntent.Refresh -> refresh(force = true)
            is NewsFeedIntent.ArticleClick -> _sideEffects.tryEmit(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
        }
    }

    private fun observePreferencesChanges() {
        viewModelScope.launch {
            userPreferencesRepository.observeUserPreferences()
                .map { it.defaultCountry to it.defaultLanguage }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    refresh(force = true)
                }
        }
    }

    private fun refresh(force: Boolean = false) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            if (_feedLoadState.value !is FeedLoadState.Success) {
                _feedLoadState.value = FeedLoadState.Loading
            }
            when (val result = newsFeedRepository.refreshFeed(force)) {
                is AppResult.Success -> {
                    when (result.data) {

                        is RefreshFeedResult.Updated,
                        RefreshFeedResult.CacheFresh -> {
                            _feedLoadState.value = FeedLoadState.Success
                        }

                        is RefreshFeedResult.IncompatibleLocale -> {
                            _sideEffects.tryEmit(
                                NewsFeedSideEffect.ShowWarning(
                                    UiText.StringResource(R.string.locale_incompatible)
                                )
                            )
                            _feedLoadState.value = FeedLoadState.Success
                        }
                    }
                }

                is AppResult.Failure -> {
                    _sideEffects.tryEmit(NewsFeedSideEffect.ShowError(result.error))
                    _feedLoadState.value = FeedLoadState.Error(result.error)
                }
            }
        }
    }
}

