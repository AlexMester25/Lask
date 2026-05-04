package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.entity.ArticleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookmarksLocalDataSource(
    private val userStateDao: ArticleUserStateDao,
) {

    fun observeBookmarkedArticles(): Flow<List<ArticleEntity>> =
        userStateDao.observeBookmarkedArticles()

    suspend fun removeBookmarks(ids: Set<Long>) =
        ids.forEach { articleId ->
            userStateDao.updateBookmark(
                articleId = articleId,
                isBookmarked = false,
                bookmarkedAt = null,
            )
        }
}