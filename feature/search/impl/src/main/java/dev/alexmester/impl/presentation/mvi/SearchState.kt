package dev.alexmester.impl.presentation.mvi

import dev.alexmester.impl.domain.model.FilterType
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsArticle

data class SearchState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val openedFilterType: FilterType? = null,
    val results: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadMoreError: Boolean = false,
    val endReached: Boolean = false,
    val error: NetworkError? = null,
    val hasSearched: Boolean = false,
)