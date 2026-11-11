package dev.alexmester.impl.domain.repository

import dev.alexmester.impl.domain.model.ExploreQuery
import dev.alexmester.impl.domain.model.RefreshExploreResult
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {

    fun observeArticles(): Flow<List<NewsArticle>>

    suspend fun getExploreQuery(): ExploreQuery

    suspend fun refresh(
        force: Boolean,
        query: String,
        language: String,
    ): AppResult<RefreshExploreResult>

    suspend fun loadMore(
        query: String,
        language: String,
        offset: Int,
    ): AppResult<RefreshExploreResult>

    suspend fun getLastCachedAt(): Long?
}