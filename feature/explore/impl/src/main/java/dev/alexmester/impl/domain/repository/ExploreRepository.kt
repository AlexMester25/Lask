package dev.alexmester.impl.domain.repository

import dev.alexmester.impl.domain.model.ExploreQuery
import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {

    fun observeArticles(): Flow<List<NewsArticle>>

    fun observeReadArticleIds(): Flow<List<Long>>

    suspend fun getExploreQuery(): ExploreQuery

    suspend fun refresh(
        force: Boolean,
        query: String,
        language: String,
    ): AppResult<RefreshResult>

    suspend fun loadMore(
        query: String,
        language: String,
        offset: Int,
    ): AppResult<RefreshResult>

    suspend fun getLastCachedAt(): Long?
}