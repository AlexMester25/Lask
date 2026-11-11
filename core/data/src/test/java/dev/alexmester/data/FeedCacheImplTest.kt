package dev.alexmester.data

import dev.alexmester.data.cache.FeedCacheImpl
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.FeedArticleWithState
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.dao.TransactionRunner
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.domain.cache.FeedType
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class FeedCacheImplTest {

    private val now = 10_000L
    private val ttl = 1_000L
    private val clock = Clock.fixed(Instant.ofEpochMilli(now), ZoneOffset.UTC)
    private val calls = mutableListOf<String>()

    private fun cache(dao: FakeFeedCacheDao) = FeedCacheImpl(
        transactionRunner = object : TransactionRunner {
            override suspend fun invoke(tx: suspend () -> Unit) = tx()
        },
        articleDao = FakeArticleDao(calls),
        feedCacheDao = dao,
        clock = clock,
    )

    @Test
    fun `no cache is not fresh`() = runTest {
        assertFalse(cache(FakeFeedCacheDao(null)).isFresh(FeedType.TRENDS, ttl))
    }

    @Test
    fun `younger than ttl is fresh`() = runTest {
        assertTrue(cache(FakeFeedCacheDao(now - ttl + 1)).isFresh(FeedType.TRENDS, ttl))
    }

    @Test
    fun `exactly ttl old is stale`() = runTest {
        assertFalse(cache(FakeFeedCacheDao(now - ttl)).isFresh(FeedType.TRENDS, ttl))
    }

    @Test
    fun `older than ttl is stale`() = runTest {
        assertFalse(cache(FakeFeedCacheDao(now - ttl - 1)).isFresh(FeedType.TRENDS, ttl))
    }

    @Test
    fun `replace stamps entries with clock time`() = runTest {
        val dao = FakeFeedCacheDao(null)

        cache(dao).replace(FeedType.TRENDS, listOf(NewsCluster(id = 0, articles = listOf(article()))))

        assertEquals(listOf(now), dao.inserted.map { it.cachedAt })
    }

    /**
     * article_user_state ссылается на articles без CASCADE, поэтому состояние без
     * пользовательских данных нужно удалять до удаления осиротевших статей,
     * иначе deleteOrphaned() падает на FOREIGN KEY constraint.
     */
    @Test
    fun `replace cleans inactive user state before deleting orphaned articles`() = runTest {
        cache(FakeFeedCacheDao(null, calls))
            .replace(FeedType.TRENDS, listOf(NewsCluster(id = 0, articles = listOf(article()))))

        assertEquals(
            listOf(
                "clearFeed",
                "insertArticles",
                "insertFeedCache",
                "deleteOrphanedInactiveUserStates",
                "deleteOrphaned",
            ),
            calls,
        )
    }

    private fun article() = NewsArticle(
        id = 1L, title = "t", text = null, summary = null, url = "u", image = null,
        video = null, publishDate = "2026-10-22 10:00:00", authors = emptyList(),
        category = null, language = null, sourceCountry = null, sentiment = null,
    )
}

private class FakeFeedCacheDao(
    private val lastCachedAt: Long?,
    private val calls: MutableList<String> = mutableListOf(),
) : FeedCacheDao {
    val inserted = mutableListOf<FeedCacheEntity>()

    override suspend fun insertFeedCache(entries: List<FeedCacheEntity>) {
        calls += "insertFeedCache"
        inserted += entries
    }

    override suspend fun clearFeed(feedType: String) {
        calls += "clearFeed"
    }

    override fun observeFeedWithState(feedType: String): Flow<List<FeedArticleWithState>> = emptyFlow()
    override suspend fun getLastCachedAt(feedType: String): Long? = lastCachedAt
}

private class FakeArticleDao(private val calls: MutableList<String>) : ArticleDao {
    override suspend fun insertArticles(articles: List<ArticleEntity>) {
        calls += "insertArticles"
    }

    override suspend fun getArticleById(id: Long): ArticleEntity? = null

    override suspend fun deleteOrphanedInactiveUserStates() {
        calls += "deleteOrphanedInactiveUserStates"
    }

    override suspend fun deleteOrphaned() {
        calls += "deleteOrphaned"
    }
}
