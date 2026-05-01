package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.impl.domain.usecase.GetReadArticleIdsSearchUseCase
import dev.alexmester.impl.domain.usecase.SearchUseCase
import dev.alexmester.models.result.onFailure
import dev.alexmester.models.result.onSuccess
import dev.alexmester.utils.constants.LaskConstants.PAGE_SIZE
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEBOUNCE_MS = 500L
private const val MIN_QUERY_LENGTH = 2

class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val getReadArticleIdsSearchUseCase: GetReadArticleIdsSearchUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _sideEffects = Channel<SearchSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    val readArticleIds: StateFlow<Set<Long>> =
        getReadArticleIdsSearchUseCase()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    private var debounceJob: Job? = null
    private var loadMoreJob: Job? = null

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> onQueryChanged(intent.query)
            is SearchIntent.FiltersChanged -> onFiltersChanged(intent.filters)
            is SearchIntent.Search -> performSearch()
            is SearchIntent.LoadMore -> loadMore()
            is SearchIntent.RetryLoadMore -> retryLoadMore()
            is SearchIntent.Cancel -> emitSideEffect(SearchSideEffect.NavigateBack)
            is SearchIntent.ArticleClick -> emitSideEffect(
                SearchSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
            is SearchIntent.ClearQuery -> {
                _state.update { it.copy(query = "", results = emptyList(), hasSearched = false, error = null) }
                debounceJob?.cancel()
            }
        }
    }

    private fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query, error = null) }
        debounceJob?.cancel()
        if (query.length < MIN_QUERY_LENGTH) {
            _state.update { it.copy(results = emptyList(), hasSearched = false) }
            return
        }
        debounceJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            performSearch()
        }
    }

    private fun onFiltersChanged(filters: SearchFilters) {
        _state.update { it.copy(filters = filters) }
        if (_state.value.query.length >= MIN_QUERY_LENGTH) {
            debounceJob?.cancel()
            performSearch()
        }
    }

    private fun performSearch() {
        val current = _state.value
        if (current.query.length < MIN_QUERY_LENGTH) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            searchUseCase(
                current.query,
                current.filters
            ).onSuccess { result ->
                _state.update {
                    it.copy(results = result, isLoading = false, hasSearched = true)
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasSearched = true,
                        error = error,
                    )
                }
            }
        }
    }

    private fun loadMore() {
        val current = _state.value
        if (current.query.length < MIN_QUERY_LENGTH) return
        if (loadMoreJob?.isActive == true) return

        loadMoreJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, loadMoreError = false) }
            searchUseCase(
                query = current.query,
                filters = current.filters,
                offset = current.results.size,
            ).onSuccess { result ->
                _state.update {
                    it.copy(
                        results = it.results + result,
                        isLoadingMore = false,
                        endReached = result.size < PAGE_SIZE,
                    )
                }
            }.onFailure {
                _state.update {
                    it.copy(isLoadingMore = false, loadMoreError = true)
                }
            }
        }
    }

    private fun retryLoadMore() {
        _state.update { it.copy(loadMoreError = false) }
        loadMore()
    }

    private fun emitSideEffect(effect: SearchSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}