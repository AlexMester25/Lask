package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.impl.domain.repository.SearchRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult

class SearchUseCase(
    private val repository: SearchRepository,
) {
    operator suspend fun invoke(
        query: String,
        filters: SearchFilters,
        offset: Int = 0,
    ): AppResult<List<NewsArticle>> = repository.search(
        query = query,
        filters = filters,
        offset = offset,
    )
}