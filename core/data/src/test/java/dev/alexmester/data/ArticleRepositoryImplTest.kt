package dev.alexmester.data

import dev.alexmester.data.repository.ArticleRepositoryImpl
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.TransactionRunner
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.ArticleUserStateEntity
import dev.alexmester.platform.dispatchers.DispatcherProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ArticleRepositoryImplTest {

    private val clock = Clock.fixed(Instant.ofEpochMilli(10_000L), ZoneOffset.UTC)
    private val userStateDao = FakeArticleUserStateDao()

    private fun repository() = ArticleRepositoryImpl(
        userStateDao = userStateDao,
        articleDao = StubArticleDao(),
        transactionRunner = object : TransactionRunner {
            override suspend fun invoke(tx: suspend () -> Unit) = tx()
        },
        dispatchers = object : DispatcherProvider {
            override val main = Dispatchers.Unconfined
            override val io = Dispatchers.Unconfined
            override val default = Dispatchers.Unconfined
        },
        clock = clock,
    )

    @Test
    fun `bookmark toggled off removes the empty state row`() = runTest {
        val repository = repository()

        assertTrue(repository.toggleBookmark(1L))
        assertFalse(repository.toggleBookmark(1L))

        assertNull(userStateDao.rows[1L])
    }

    @Test
    fun `bookmark toggled off keeps state that still has read data`() = runTest {
        val repository = repository()
        repository.markAsRead(1L)
        repository.toggleBookmark(1L)

        assertFalse(repository.toggleBookmark(1L))

        assertEquals(true, userStateDao.rows[1L]?.isRead)
    }

    @Test
    fun `bookmark toggled off keeps state that still has claps`() = runTest {
        val repository = repository()
        repository.addClap(1L)
        repository.toggleBookmark(1L)

        assertFalse(repository.toggleBookmark(1L))

        assertEquals(1, userStateDao.rows[1L]?.clapCount)
    }

    @Test
    fun `remove bookmarks deletes only rows without user data`() = runTest {
        val repository = repository()
        repository.toggleBookmark(1L) // пустая после снятия закладки
        repository.toggleBookmark(2L)
        repository.toggleBookmark(3L)
        repository.markAsRead(2L)
        repository.addClap(3L)

        repository.removeBookmarks(setOf(1L, 2L, 3L))

        assertNull(userStateDao.rows[1L])
        assertEquals(true, userStateDao.rows[2L]?.isRead)
        assertEquals(1, userStateDao.rows[3L]?.clapCount)
    }
}

private class FakeArticleUserStateDao : ArticleUserStateDao {
    val rows = mutableMapOf<Long, ArticleUserStateEntity>()

    override suspend fun insertIfAbsent(state: ArticleUserStateEntity) {
        rows.putIfAbsent(state.articleId, state)
    }

    override suspend fun toggleBookmark(articleId: Long, now: Long) {
        val row = rows[articleId] ?: return
        val bookmarked = !row.isBookmarked
        rows[articleId] = row.copy(
            isBookmarked = bookmarked,
            bookmarkedAt = if (bookmarked) now else null,
        )
    }

    override suspend fun clearBookmarks(ids: Set<Long>) {
        ids.forEach { id ->
            rows[id]?.let { rows[id] = it.copy(isBookmarked = false, bookmarkedAt = null) }
        }
    }

    override suspend fun deleteEmptyStates(ids: Set<Long>) {
        ids.forEach { id ->
            val row = rows[id] ?: return@forEach
            val isEmpty = !row.isBookmarked && !row.isRead && row.clapCount == 0 &&
                row.bookmarkedAt == null && row.readAt == null
            if (isEmpty) rows.remove(id)
        }
    }

    override suspend fun markAsRead(articleId: Long, readAt: Long): Int {
        val row = rows[articleId] ?: return 0
        if (row.isRead) return 0
        rows[articleId] = row.copy(isRead = true, readAt = readAt)
        return 1
    }

    override suspend fun incrementClap(articleId: Long) {
        rows[articleId]?.let { rows[articleId] = it.copy(clapCount = it.clapCount + 1) }
    }

    override fun observeIsBookmarked(articleId: Long): Flow<Boolean?> =
        flowOf(rows[articleId]?.isBookmarked)

    override suspend fun isBookmarked(articleId: Long): Boolean? = rows[articleId]?.isBookmarked

    override fun observeBookmarkedArticles(): Flow<List<ArticleEntity>> = emptyFlow()
    override fun observeReadArticleIds(): Flow<List<Long>> = emptyFlow()
    override fun observeReadCount(): Flow<Int> = emptyFlow()
    override fun observeReadArticles(): Flow<List<ArticleEntity>> = emptyFlow()
    override fun observeClapCount(articleId: Long): Flow<Int?> = flowOf(rows[articleId]?.clapCount)
    override fun observeClappedArticles(): Flow<List<ArticleEntity>> = emptyFlow()
}

private class StubArticleDao : ArticleDao {
    override suspend fun insertArticles(articles: List<ArticleEntity>) = Unit
    override suspend fun getArticleById(id: Long): ArticleEntity? = null
    override suspend fun deleteOrphanedInactiveUserStates() = Unit
    override suspend fun deleteOrphaned() = Unit
}
