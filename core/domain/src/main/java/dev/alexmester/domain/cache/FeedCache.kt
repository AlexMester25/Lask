package dev.alexmester.domain.cache

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

enum class FeedType { TRENDS, EXPLORE }

interface FeedCache {
    fun observeClusters(feedType: FeedType): Flow<List<NewsCluster>>

    /**
     * Заменяет ленту целиком: clear, вставка статей, вставка связей, удаление «сирот». Одна транзакция.
     * Сбой хранилища возвращается как [AppResult.Failure].
     */
    suspend fun replace(feedType: FeedType, clusters: List<NewsCluster>): AppResult<Unit>

    /** Дописывает статьи в конец ленты (пагинация). Сбой хранилища возвращается как [AppResult.Failure]. */
    suspend fun append(
        feedType: FeedType,
        articles: List<NewsArticle>,
        positionStart: Int,
    ): AppResult<Unit>

    suspend fun isFresh(feedType: FeedType, ttlMs: Long): Boolean

    suspend fun lastCachedAt(feedType: FeedType): Long?
}