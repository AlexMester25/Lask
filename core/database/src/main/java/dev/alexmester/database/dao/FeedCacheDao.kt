package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.FeedCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedCache(entries: List<FeedCacheEntity>)

    @Query("DELETE FROM feed_cache WHERE feedType = :feedType")
    suspend fun clearFeed(feedType: String)

    @Query("""
    SELECT a.*,
           f.clusterId,
           f.position
    FROM feed_cache f
    INNER JOIN articles a ON f.articleId = a.id
    WHERE f.feedType = :feedType
    ORDER BY f.clusterId ASC, f.position ASC
    """)
    fun observeFeedWithState(feedType: String): Flow<List<FeedArticleWithState>>

    @Query("SELECT MAX(cachedAt) FROM feed_cache WHERE feedType = :feedType")
    suspend fun getLastCachedAt(feedType: String): Long?
}


