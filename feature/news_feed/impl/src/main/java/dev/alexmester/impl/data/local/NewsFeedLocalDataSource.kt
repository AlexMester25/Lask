package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.entity.NewsArticleEntity
import dev.alexmester.database.entity.NewsArticleEntity.Companion.SOURCE_FEED
import kotlinx.coroutines.flow.Flow

class NewsFeedLocalDataSource(private val dao: NewsArticleDao) {

    fun getArticles(): Flow<List<NewsArticleEntity>> =
        dao.getArticlesBySource(SOURCE_FEED)

    suspend fun getLastCachedAt(): Long? =
        dao.getLastCachedAt(SOURCE_FEED)

    suspend fun saveArticles(articles: List<NewsArticleEntity>) =
        dao.insertArticles(articles)

    suspend fun clearArticles() =
        dao.clearBySource(SOURCE_FEED)

    suspend fun replaceArticles(articles: List<NewsArticleEntity>) =
        dao.replaceArticles(SOURCE_FEED, articles)
}