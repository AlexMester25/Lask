package dev.alexmester.impl.presentstion.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.impl.domain.interactor.ExploreInteractor
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val interactor: ExploreInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    private val _sideEffects = Channel<ExploreSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    val readArticleIds: StateFlow<Set<Long>> = interactor.observeReadArticleIds()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    private val pageSize = 20

    init {
        observeLocalCache()
        bootstrap()
    }

    fun handleIntent(intent: ExploreIntent) {
        when (intent) {
            ExploreIntent.Refresh -> refresh()
            ExploreIntent.LoadMore -> loadMore()
            is ExploreIntent.ArticleClick -> {
                emitSideEffect(
                    ExploreSideEffect.NavigateToArticle(
                        articleId = intent.articleId,
                        articleUrl = intent.articleUrl,
                    )
                )
            }
        }
    }

    private fun observeLocalCache() {
        interactor.observeArticles()
            .map { articles ->
                val current = _state.value
                if (articles.isEmpty()) return@map current

                val content = (current as? ExploreState.Content)
                ExploreState.Content(
                    articles = articles,
                    isRefreshing = false,
                    isLoadingMore = false,
                    endReached = content?.endReached ?: false,
                    lastCachedAt = content?.lastCachedAt,
                    isOffline = content?.isOffline ?: false,
                )
            }
            .onEach { mapped ->
                _state.value = mapped
            }
            .launchIn(viewModelScope)
    }

    private fun bootstrap() {
        viewModelScope.launch {
            val query = interactor.getExploreQueryOrNull()
            if (query.isNullOrBlank()) {
                _state.value = ExploreState.EmptyInterests
                return@launch
            }

            val cachedAt = interactor.getLastCachedAt()
            val currentContent = _state.value as? ExploreState.Content
            if (currentContent != null) {
                _state.value = currentContent.copy(lastCachedAt = cachedAt)
            }

            if (currentContent == null || currentContent.articles.isEmpty()) {
                _state.value = ExploreState.Loading
            }
            refresh()
        }
    }

    private fun refresh() {
        _state.update { state ->
            when (state) {
                is ExploreState.Content -> state.copy(isRefreshing = true, isOffline = false)
                is ExploreState.Error -> state.copy(isRefreshing = true)
                else -> state
            }
        }

        viewModelScope.launch {
            when (val result = interactor.refresh(pageSize = pageSize)) {
                is AppResult.Success -> {
                    val cachedAt = interactor.getLastCachedAt()
                    val current = _state.value
                    if (current is ExploreState.Content) {
                        _state.value = current.copy(
                            isRefreshing = false,
                            isLoadingMore = false,
                            endReached = result.data < pageSize,
                            lastCachedAt = cachedAt,
                            isOffline = false,
                        )
                    }
                }

                is AppResult.Failure -> {
                    handleError(result.error)
                }
            }
        }
    }

    private fun loadMore() {
        val current = _state.value as? ExploreState.Content ?: return
        if (current.isLoadingMore || current.isRefreshing || current.endReached) return

        _state.value = current.copy(isLoadingMore = true)

        viewModelScope.launch {
            when (val result = interactor.loadMore(pageSize = pageSize, offset = current.articles.size)) {
                is AppResult.Success -> {
                    val stateNow = _state.value as? ExploreState.Content ?: return@launch
                    _state.value = stateNow.copy(
                        isLoadingMore = false,
                        endReached = result.data < pageSize,
                        isOffline = false,
                    )
                }

                is AppResult.Failure -> {
                    val stateNow = _state.value as? ExploreState.Content ?: return@launch
                    _state.value = stateNow.copy(isLoadingMore = false)
                    val errorMessage = NetworkErrorUiMapper.toUiText(result.error)
                    emitSideEffect(ExploreSideEffect.ShowError(errorMessage))
                }
            }
        }
    }

    private fun emitSideEffect(effect: ExploreSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }

    private fun handleError(error: NetworkError) {
        viewModelScope.launch {
            val message = NetworkErrorUiMapper.toUiText(error)
            val current = _state.value

            _state.value = when {
                error is NetworkError.NoInternet && current is ExploreState.Content && current.articles.isNotEmpty() -> {
                    current.copy(isRefreshing = false, isOffline = true)
                }

                current is ExploreState.Content -> current.copy(isRefreshing = false)
                else -> ExploreState.Error(message = message)
            }

            emitSideEffect(ExploreSideEffect.ShowError(message))
        }
    }
}