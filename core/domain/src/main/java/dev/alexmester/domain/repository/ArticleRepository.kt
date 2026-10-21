package dev.alexmester.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun observeReadArticleIds(): Flow<Set<Long>>
    fun observeReadArticles(): Flow<List<NewsArticle>>
    fun observeClappedArticles(): Flow<List<NewsArticle>>
    suspend fun saveArticles(articles: List<NewsArticle>)

    suspend fun getArticleById(id: Long): NewsArticle?

    fun observeBookmarkedArticles(): Flow<List<NewsArticle>>
    fun observeIsBookmarked(articleId: Long): Flow<Boolean>
    suspend fun toggleBookmark(articleId: Long): Boolean
    suspend fun removeBookmarks(ids: Set<Long>)

    fun observeClapCount(articleId: Long): Flow<Int>
    suspend fun addClap(articleId: Long)

    suspend fun markAsRead(articleId: Long): Boolean
    fun observeReadCount(): Flow<Int>
}