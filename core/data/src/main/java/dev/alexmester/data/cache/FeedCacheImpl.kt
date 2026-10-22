package dev.alexmester.data.cache

import dev.alexmester.data.mapper.toClusters
import dev.alexmester.data.mapper.toEntities
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.dao.TransactionRunner
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.domain.cache.FeedCache
import dev.alexmester.domain.cache.FeedType
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock

internal class FeedCacheImpl(
    private val transactionRunner: TransactionRunner,
    private val articleDao: ArticleDao,
    private val feedCacheDao: FeedCacheDao,
    private val clock: Clock,
) : FeedCache {

    override fun observeClusters(feedType: FeedType): Flow<List<NewsCluster>> =
        feedCacheDao.observeFeedWithState(feedType.key).map { it.toClusters() }

    override suspend fun replace(feedType: FeedType, clusters: List<NewsCluster>) {
        val (articles, cache) = clusters.toEntities(feedType.key, clock.millis())
        transactionRunner {
            feedCacheDao.clearFeed(feedType.key)
            articleDao.insertArticles(articles)
            feedCacheDao.insertFeedCache(cache)
            articleDao.deleteOrphaned()
        }
    }

    override suspend fun append(feedType: FeedType, articles: List<NewsArticle>, positionStart: Int) {
        val (entities, cache) = articles.toEntities(feedType.key, positionStart, clock.millis())
        transactionRunner {
            articleDao.insertArticles(entities)
            feedCacheDao.insertFeedCache(cache)
            articleDao.deleteOrphaned()
        }
    }

    override suspend fun isFresh(feedType: FeedType, ttlMs: Long): Boolean {
        val cachedAt = feedCacheDao.getLastCachedAt(feedType.key) ?: return false
        return clock.millis() - cachedAt < ttlMs
    }

    override suspend fun lastCachedAt(feedType: FeedType): Long? =
        feedCacheDao.getLastCachedAt(feedType.key)

    private val FeedType.key: String
        get() = when (this) {
            FeedType.TRENDS -> FeedCacheEntity.TRENDS_FEED
            FeedType.EXPLORE -> FeedCacheEntity.EXPLORE_FEED
        }
}