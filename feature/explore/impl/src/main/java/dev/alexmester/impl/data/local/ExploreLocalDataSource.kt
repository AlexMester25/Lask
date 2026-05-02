package dev.alexmester.impl.data.local

import androidx.room.withTransaction
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.database.entity.FeedCacheEntity.Companion.EXPLORE_FEED
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExploreLocalDataSource(
    private val db: AppDatabase,
    private val articleDao: ArticleDao,
    private val feedCacheDao: FeedCacheDao,
    private val userStateDao: ArticleUserStateDao,
) {

    fun observeFeedArticles(): Flow<List<NewsArticle>> =
        feedCacheDao.observeFeedWithState(EXPLORE_FEED)
            .map { rows -> rows.sortedBy { it.position }.map { it.toDomain() } }

    fun observeReadArticleIds(): Flow<List<Long>> = userStateDao.observeReadArticleIds()

    suspend fun refreshFeed(
        articles: List<ArticleEntity>,
        feedCache: List<FeedCacheEntity>,
    ) {
        db.withTransaction {
            feedCacheDao.clearFeed(EXPLORE_FEED)
            articleDao.insertArticles(articles)
            feedCacheDao.insertFeedCache(feedCache)
            articleDao.deleteOrphaned()
        }
    }

    suspend fun loadMoreFeed(
        articles: List<ArticleEntity>,
        feedCache: List<FeedCacheEntity>,
    ) {
        db.withTransaction {
            articleDao.insertArticles(articles)
            feedCacheDao.insertFeedCache(feedCache)
            articleDao.deleteOrphaned()
        }
    }

    suspend fun getLastCachedAt(): Long? =
        feedCacheDao.getLastCachedAt(EXPLORE_FEED)

}