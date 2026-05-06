package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.dao.TransactionRunner
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.database.entity.FeedCacheEntity.Companion.TRENDS_FEED
import dev.alexmester.impl.data.mapper.toClusters
import dev.alexmester.models.news.NewsCluster
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsFeedLocalDataSource(
    private val transactionRunner: TransactionRunner,
    private val articleDao: ArticleDao,
    private val feedCacheDao: FeedCacheDao,
    private val userStateDao: ArticleUserStateDao,
) {

    fun observeFeedClusters(): Flow<List<NewsCluster>> =
        feedCacheDao.observeFeedWithState(TRENDS_FEED)
            .map { rows -> rows.toClusters() }

    fun observeReadArticleIds(): Flow<List<Long>> =
        userStateDao.observeReadArticleIds()

    suspend fun getLastCachedAt(): Long? =
        feedCacheDao.getLastCachedAt(TRENDS_FEED)

    suspend fun replaceFeedCache(
        articles: List<ArticleEntity>,
        feedCache: List<FeedCacheEntity>,
    ) = transactionRunner {
        feedCacheDao.clearFeed(TRENDS_FEED)
        articleDao.insertArticles(articles)
        feedCacheDao.insertFeedCache(feedCache)
        articleDao.deleteOrphaned()
    }
}
