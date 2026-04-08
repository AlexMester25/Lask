package dev.alexmester.impl.data.local

import androidx.room.withTransaction
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.database.entity.NewsArticleEntity
import dev.alexmester.database.entity.NewsArticleEntity.Companion.SOURCE_FEED
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NewsFeedLocalDataSource(
    private val newsArticleDao: NewsArticleDao,
    private val readingHistoryDao: ReadingHistoryDao,
    private val db: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun getArticles(): Flow<List<NewsArticleEntity>> =
        newsArticleDao.getArticlesBySource(SOURCE_FEED)

    suspend fun getLastCachedAt(): Long? =
        withContext(ioDispatcher) {
            newsArticleDao.getLastCachedAt(SOURCE_FEED)
        }

    suspend fun replaceArticles(articles: List<NewsArticleEntity>) =
        withContext(ioDispatcher) {
            db.withTransaction {
                newsArticleDao.clearBySource(SOURCE_FEED)
                newsArticleDao.insertArticles(articles)
            }
        }

    fun getReadArticleIds(): Flow<List<Long>> =
        readingHistoryDao.getReadArticleIds()
}