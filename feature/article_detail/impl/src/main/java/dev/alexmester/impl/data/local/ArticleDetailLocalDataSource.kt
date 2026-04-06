package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.BookmarkDao
import dev.alexmester.database.dao.ClapDao
import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.database.entity.BookmarkEntity
import dev.alexmester.database.entity.ClapEntity
import dev.alexmester.database.entity.NewsArticleEntity
import dev.alexmester.database.entity.ReadingHistoryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ArticleDetailLocalDataSource(
    private val newsArticleDao: NewsArticleDao,
    private val bookmarkDao: BookmarkDao,
    private val clapDao: ClapDao,
    private val readingHistoryDao: ReadingHistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun getArticleById(id: Long): NewsArticleEntity? =
        withContext(ioDispatcher) { newsArticleDao.getArticleById(id) }

    suspend fun getBookmarkById(id: Long): BookmarkEntity? =
        withContext(ioDispatcher) { bookmarkDao.getBookmarkById(id) }

    suspend fun getClapCountById(id: Long): Int? =
        withContext(ioDispatcher) { clapDao.getClapCount(id) }

    fun isBookmarkedFlow(id: Long): Flow<Boolean> = bookmarkDao.isBookmarked(id)

    fun getClapFlow(id: Long): Flow<Int> =
        clapDao.getClapFlow(id).map { it?.count ?: 0 }

    suspend fun insertBookmark(bookmark: BookmarkEntity) =
        withContext(ioDispatcher) { bookmarkDao.insertBookmark(bookmark) }

    suspend fun deleteBookmark(id: Long) =
        withContext(ioDispatcher) { bookmarkDao.deleteBookmark(id) }



    suspend fun addClap(id: Long) = withContext(ioDispatcher) {
        val existing = clapDao.getClapCount(id)
        if (existing == null) {
            clapDao.upsert(ClapEntity(articleId = id, count = 1))
        } else {
            clapDao.increment(id)
        }
    }

    suspend fun markAsRead(articleId: Long, articleTitle: String) =
        withContext(ioDispatcher) {
            readingHistoryDao.upsert(
                ReadingHistoryEntity(
                    articleId = articleId,
                    articleTitle = articleTitle,
                    readAt = System.currentTimeMillis(),
                )
            )
        }
}