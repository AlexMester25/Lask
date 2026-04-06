package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.BookmarkDao
import dev.alexmester.database.entity.BookmarkEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookmarksLocalDataSource(
    private val dao: BookmarkDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = dao.getAllBookmarks()

    suspend fun deleteBookmarks(ids: Set<Long>) = withContext(ioDispatcher) {
        ids.forEach { dao.deleteBookmark(it) }
    }
}