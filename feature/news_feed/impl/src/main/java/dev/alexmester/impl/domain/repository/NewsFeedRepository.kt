package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {

    fun getClustersFlow(): Flow<List<NewsCluster>>

    suspend fun refreshTopNews(
        country: String,
        language: String,
        forceRefresh: Boolean = false,
    ): AppResult<Unit>

    suspend fun getLastCachedAt(): Long?
}