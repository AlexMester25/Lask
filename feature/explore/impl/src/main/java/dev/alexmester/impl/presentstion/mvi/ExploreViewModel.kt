package dev.alexmester.impl.presentstion.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.impl.domain.usecase.GetLastCachedAtExploreUseCase
import dev.alexmester.impl.domain.usecase.LoadMoreExploreUseCase
import dev.alexmester.impl.domain.usecase.ObserveArticlesExploreUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsExploreUseCase
import dev.alexmester.impl.domain.usecase.RefreshExploreUseCase
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.onFailure
import dev.alexmester.models.result.onSuccess
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import dev.alexmester.utils.constants.LaskConstants.PAGE_SIZE
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val refreshExplore: RefreshExploreUseCase,
    private val loadMore: LoadMoreExploreUseCase,
    private val observeArticles: ObserveArticlesExploreUseCase,
    private val observeReadIds: ObserveReadArticleIdsExploreUseCase,
    private val getLastCachedAt: GetLastCachedAtExploreUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    private val _sideEffects = Channel<ExploreSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    val readArticleIds: StateFlow<Set<Long>> =
        observeReadIds()
            .map { it.toSet() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptySet()
            )

    init {
        bootstrap()
        observeExploreCache()
    }

    fun handleIntent(intent: ExploreIntent) {
        when (intent) {
            is ExploreIntent.Refresh -> refresh()
            is ExploreIntent.LoadMore -> loadMore()
            is ExploreIntent.RetryLoadMore -> retryLoadMore()
            is ExploreIntent.ArticleClick ->
                emitSideEffect(
                    ExploreSideEffect.NavigateToArticle(
                        intent.articleId,
                        intent.articleUrl
                    )
                )
        }
    }

    private fun observeExploreCache() {
        observeArticles().onEach { articles ->
            if (articles.isEmpty()) return@onEach
            _state.update { current ->
                if (current is ExploreState.Content) current.copy(articles = articles)
                else current
            }
        }.launchIn(viewModelScope)
    }

    private fun bootstrap() {
        viewModelScope.launch {
            _state.value = ExploreState.Loading

            val cached = observeArticles().first()
            if (cached.isNotEmpty()) {
                _state.value = ExploreState.Content(articles = cached)
            }
            refresh()
        }
    }

    private fun refresh() {
        _state.update { it.withRefreshing(true) }

        viewModelScope.launch {
            refreshExplore()
                .onSuccess { result ->
                    handleRefreshResult(result)
                }
                .onFailure { error ->
                    handleError(error)
                }
        }
    }

    private fun retryLoadMore() {
        _state.updateContent { it.copy(isLoadingMore = false, loadMoreError = false) }
        loadMore()
    }

    private fun loadMore() {
        val current = _state.value.contentOrNull ?: run {
            return
        }
        if (current.isLoadingMore || current.isRefreshing || current.endReached) {
            return
        }

        _state.updateContent { it.copy(isLoadingMore = true, loadMoreError = false) }

        viewModelScope.launch {
            loadMore(offset = current.articles.size)
                .onSuccess { result ->
                    handleLoadMoreResult(result)
                }
                .onFailure { error ->
                    _state.updateContent { it.copy(isLoadingMore = false, loadMoreError = true) }
                    emitSideEffect(ExploreSideEffect.ShowError(NetworkErrorUiMapper.toUiText(error)))
                }
        }
    }

    private fun handleRefreshResult(result: RefreshResult) {
        val hasCache = _state.value.isContent

        when (result) {
            is RefreshResult.NoInterests -> {
                _state.updateContent { it.copy(isLoadingMore = false, loadMoreError = true) }
                handleEmptyResult(
                    hasCache = hasCache,
                    warningRes = R.string.interests_you_not_added,
                    emptyState = ExploreState.EmptyInterests(isRefreshing = false),
                )
            }

            is RefreshResult.EmptySearchResult -> {
                _state.updateContent { it.copy(isLoadingMore = false, loadMoreError = true) }
                handleEmptyResult(
                    hasCache = hasCache,
                    warningRes = R.string.warning_explore_content_empty,
                    emptyState = ExploreState.EmptyResult(isRefreshing = false),
                )
            }

            is RefreshResult.Success -> _state.update { current ->
                ExploreState.Content(
                    articles = current.contentOrNull?.articles.orEmpty(),
                    isRefreshing = false,
                    isLoadingMore = false,
                    endReached = result.count < PAGE_SIZE,
                    refreshId = (current.contentOrNull?.refreshId ?: 0) + 1,
                )
            }
        }
    }

    private fun handleLoadMoreResult(result: RefreshResult) {
        when (result) {
            is RefreshResult.NoInterests -> {
                _state.updateContent { it.copy(isLoadingMore = false, loadMoreError = true) }
                emitSideEffect(
                    ExploreSideEffect.ShowWarning(
                        UiText.StringResource(R.string.interests_you_not_added)
                    )
                )
            }
            is RefreshResult.EmptySearchResult -> {
                _state.updateContent { it.copy(isLoadingMore = false, loadMoreError = true) }
                emitSideEffect(
                    ExploreSideEffect.ShowWarning(
                        UiText.StringResource(R.string.warning_explore_content_empty)
                    )
                )
            }
            is RefreshResult.Success -> {
                _state.updateContent {
                    it.copy(
                        isLoadingMore = false,
                        endReached = result.count < PAGE_SIZE,
                    )
                }
            }
        }
    }

    private fun handleEmptyResult(
        hasCache: Boolean,
        warningRes: Int,
        emptyState: ExploreState,
    ) {
        if (hasCache) {
            _state.update { it.withRefreshing(false) }
            emitSideEffect(ExploreSideEffect.ShowWarning(UiText.StringResource(warningRes)))
        } else {
            _state.value = emptyState
        }
    }

    private fun handleError(error: NetworkError) {
        _state.update { current ->
            if (current is ExploreState.Content) current.copy(isRefreshing = false)
            else ExploreState.Error(error)
        }
        emitSideEffect(ExploreSideEffect.ShowError(NetworkErrorUiMapper.toUiText(error)))
    }

    private fun emitSideEffect(effect: ExploreSideEffect) {
        viewModelScope.launch { _sideEffects.trySend(effect) }
    }
}