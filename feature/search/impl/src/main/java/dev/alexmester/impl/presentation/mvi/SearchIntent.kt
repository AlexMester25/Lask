package dev.alexmester.impl.presentation.mvi

import dev.alexmester.impl.domain.model.FilterType
import dev.alexmester.impl.domain.model.SearchFilters

sealed class SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent()
    data class FiltersChanged(val filters: SearchFilters) : SearchIntent()
    data class OpenFilter(val filterType: FilterType): SearchIntent()
    data object ClearFilter: SearchIntent()
    data object LoadMore : SearchIntent()
    data object RetryLoadMore : SearchIntent()
    data object Search : SearchIntent()
    data object Cancel : SearchIntent()
    data class ArticleClick(val articleId: Long, val articleUrl: String) : SearchIntent()
    data object ClearQuery : SearchIntent()
}