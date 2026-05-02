package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {

    fun observeArticles(): Flow<List<NewsArticle>>

    fun observeReadArticleIds(): Flow<List<Long>>

    suspend fun refresh(
        query: String,
        language: String,
    ): AppResult<Int>

    suspend fun loadMore(
        query: String,
        language: String,
        offset: Int,
    ): AppResult<Int>

    suspend fun getLastCachedAt(): Long?
}