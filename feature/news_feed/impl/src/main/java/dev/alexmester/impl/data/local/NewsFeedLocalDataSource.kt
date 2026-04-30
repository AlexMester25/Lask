package dev.alexmester.impl.data.local

import androidx.room.withTransaction
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.database.entity.FeedCacheEntity.Companion.TRENDS_FEED
import dev.alexmester.impl.data.mapper.toClusters
import dev.alexmester.models.news.NewsCluster
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NewsFeedLocalDataSource(
    private val db: AppDatabase,
    private val articleDao: ArticleDao,
    private val feedCacheDao: FeedCacheDao,
    private val userStateDao: ArticleUserStateDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun observeFeedClusters(): Flow<List<NewsCluster>> =
        feedCacheDao.observeFeedWithState(TRENDS_FEED)
            .map { rows -> rows.toClusters() }

    fun observeReadArticleIds(): Flow<List<Long>> =
        userStateDao.observeReadArticleIds()

    suspend fun getLastCachedAt(): Long? =
        withContext(ioDispatcher) {
            feedCacheDao.getLastCachedAt(TRENDS_FEED)
        }

    suspend fun replaceFeedCache(
        articles: List<ArticleEntity>,
        feedCache: List<FeedCacheEntity>,
    ) = withContext(ioDispatcher) {
        db.withTransaction {
            feedCacheDao.clearFeed(TRENDS_FEED)
            articleDao.insertArticles(articles)
            feedCacheDao.insertFeedCache(feedCache)
            articleDao.deleteOrphaned()
        }
    }
}