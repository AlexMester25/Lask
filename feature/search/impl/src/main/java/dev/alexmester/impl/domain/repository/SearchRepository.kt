package dev.alexmester.impl.domain.repository

import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun search(
        query: String,
        filters: SearchFilters,
        offset: Int = 0,
        number: Int = 20,
    ): AppResult<List<NewsArticle>>
}