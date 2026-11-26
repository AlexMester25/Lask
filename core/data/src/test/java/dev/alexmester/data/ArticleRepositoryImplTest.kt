package dev.alexmester.data

import android.database.sqlite.SQLiteFullException
import dev.alexmester.data.repository.ArticleRepositoryImpl
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.CategoryCountRow
import dev.alexmester.database.dao.TransactionRunner
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.ArticleUserStateEntity
import dev.alexmester.domain.articles.CategoryCount
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.models.error.StorageError
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.platform.dispatchers.DispatcherProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ArticleRepositoryImplTest {

    private val clock = Clock.fixed(Instant.ofEpochMilli(10_000L), ZoneOffset.UTC)
    private val userStateDao = FakeArticleUserStateDao()

    private fun repository(articleDao: ArticleDao = StubArticleDao()) = ArticleRepositoryImpl(
        userStateDao = userStateDao,
        articleDao = articleDao,
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
    fun `save articles succeeds when dao accepts the insert`() = runTest {
        val result = repository().saveArticles(listOf(article()))

        assertTrue(result is AppResult.Success)
    }

    @Test
    fun `save articles returns storage error when dao fails`() = runTest {
        val repository = repository(
            articleDao = StubArticleDao(insertFailure = SQLiteFullException("full")),
        )

        val result = repository.saveArticles(listOf(article()))

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is StorageError.NoSpace)
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

    @Test
    fun `all filter queries read articles without category restriction`() = runTest {
        repository().observeReadArticles(CategoryFilter.All, limit = 10).first()

        assertEquals(ArticleQuery(null, onlyUncategorized = false, limit = 10), userStateDao.lastReadQuery)
    }

    @Test
    fun `uncategorized filter queries only articles without category`() = runTest {
        repository().observeReadArticles(CategoryFilter.Uncategorized, limit = 10).first()

        assertEquals(ArticleQuery(null, onlyUncategorized = true, limit = 10), userStateDao.lastReadQuery)
    }

    @Test
    fun `named filter queries its own category`() = runTest {
        repository().observeReadArticles(CategoryFilter.Named("Tech"), limit = 5).first()

        assertEquals(ArticleQuery("Tech", onlyUncategorized = false, limit = 5), userStateDao.lastReadQuery)
    }

    @Test
    fun `clapped filter is passed to the clapped query`() = runTest {
        repository().observeClappedArticles(CategoryFilter.Named("Sport"), limit = 7).first()

        assertEquals(ArticleQuery("Sport", onlyUncategorized = false, limit = 7), userStateDao.lastClappedQuery)
    }

    @Test
    fun `read category counts are mapped to domain`() = runTest {
        userStateDao.readCategoryRows = listOf(
            CategoryCountRow(category = "Tech", count = 3),
            CategoryCountRow(category = null, count = 1),
        )

        val counts = repository().observeReadCategoryCounts().first()

        assertEquals(
            listOf(CategoryCount(category = "Tech", count = 3), CategoryCount(category = null, count = 1)),
            counts,
        )
    }

    @Test
    fun `clapped category counts are mapped to domain`() = runTest {
        userStateDao.clappedCategoryRows = listOf(CategoryCountRow(category = "Sport", count = 2))

        val counts = repository().observeClappedCategoryCounts().first()

        assertEquals(listOf(CategoryCount(category = "Sport", count = 2)), counts)
    }

    private fun article() = NewsArticle(
        id = 1L, title = "t", text = null, summary = null, url = "u", image = null,
        video = null, publishDate = "2026-10-22 10:00:00", authors = emptyList(),
        category = null, language = null, sourceCountry = null, sentiment = null,
    )
}

private data class ArticleQuery(
    val category: String?,
    val onlyUncategorized: Boolean,
    val limit: Int,
)

private class FakeArticleUserStateDao : ArticleUserStateDao {
    val rows = mutableMapOf<Long, ArticleUserStateEntity>()

    var lastReadQuery: ArticleQuery? = null
    var lastClappedQuery: ArticleQuery? = null
    var readEntities: List<ArticleEntity> = emptyList()
    var clappedEntities: List<ArticleEntity> = emptyList()
    var readCategoryRows: List<CategoryCountRow> = emptyList()
    var clappedCategoryRows: List<CategoryCountRow> = emptyList()

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

    override fun observeBookmarkedArticles(limit: Int): Flow<List<ArticleEntity>> = emptyFlow()
    override fun observeBookmarkedCount(): Flow<Int> = emptyFlow()
    override fun observeReadArticleIds(): Flow<List<Long>> = emptyFlow()
    override fun observeReadCount(): Flow<Int> = emptyFlow()

    override fun observeReadArticles(
        category: String?,
        onlyUncategorized: Boolean,
        limit: Int,
    ): Flow<List<ArticleEntity>> {
        lastReadQuery = ArticleQuery(category, onlyUncategorized, limit)
        return flowOf(readEntities.take(limit))
    }

    override fun observeReadCategoryCounts(): Flow<List<CategoryCountRow>> =
        flowOf(readCategoryRows)

    override fun observeClapCount(articleId: Long): Flow<Int?> = flowOf(rows[articleId]?.clapCount)

    override fun observeClappedArticles(
        category: String?,
        onlyUncategorized: Boolean,
        limit: Int,
    ): Flow<List<ArticleEntity>> {
        lastClappedQuery = ArticleQuery(category, onlyUncategorized, limit)
        return flowOf(clappedEntities.take(limit))
    }

    override fun observeClappedCategoryCounts(): Flow<List<CategoryCountRow>> =
        flowOf(clappedCategoryRows)
}

private class StubArticleDao(
    private val insertFailure: Throwable? = null,
) : ArticleDao {
    override suspend fun insertArticles(articles: List<ArticleEntity>) {
        insertFailure?.let { throw it }
    }
    override suspend fun getArticleById(id: Long): ArticleEntity? = null
    override suspend fun deleteOrphanedInactiveUserStates() = Unit
    override suspend fun deleteOrphaned() = Unit
}
