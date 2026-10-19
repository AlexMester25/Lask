package dev.alexmester.domain.cache

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import kotlinx.coroutines.flow.Flow

enum class FeedType { TRENDS, EXPLORE }

interface FeedCache {
    fun observeClusters(feedType: FeedType): Flow<List<NewsCluster>>

    /** Заменяет ленту целиком: clear, вставка статей, вставка связей, удаление «сирот». Одна транзакция. */
    suspend fun replace(feedType: FeedType, clusters: List<NewsCluster>)

    /** Дописывает статьи в конец ленты (пагинация). */
    suspend fun append(feedType: FeedType, articles: List<NewsArticle>, positionStart: Int)

    suspend fun isFresh(feedType: FeedType, ttlMs: Long): Boolean

    suspend fun lastCachedAt(feedType: FeedType): Long?
}