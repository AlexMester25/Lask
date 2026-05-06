package dev.alexmester.impl.data.mapper

import dev.alexmester.database.dao.FeedArticleWithState
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.impl.data.remote.dto.NewsArticleDto
import dev.alexmester.impl.data.remote.dto.NewsClusterDto
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster


// ── DTO → ArticleEntity ───────────────────────────────────────────────────────

fun NewsArticleDto.toArticleEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title,
    text = text,
    summary = summary,
    url = url,
    image = image,
    video = video,
    publishDate = publishDate,
    authors = authors,
    category = category,
    language = language,
    sourceCountry = sourceCountry,
    sentiment = sentiment,
)

// ── DTO → FeedCacheEntity ─────────────────────────────────────────────────────

fun NewsArticleDto.toFeedCacheEntity(
    feedType: String,
    clusterId: Int,
    position: Int,
): FeedCacheEntity = FeedCacheEntity(
    feedType = feedType,
    articleId = id,
    clusterId = clusterId,
    position = position,
    cachedAt = System.currentTimeMillis(),
)

/**
 * Разворачиваем список кластеров в два плоских списка:
 * - все ArticleEntity (для вставки в articles с IGNORE)
 * - все FeedCacheEntity (для вставки в feed_cache с REPLACE)
 */
fun List<NewsClusterDto>.toEntities(feedType: String): Pair<List<ArticleEntity>, List<FeedCacheEntity>> {
    val articles = mutableListOf<ArticleEntity>()
    val feedCache = mutableListOf<FeedCacheEntity>()

    forEachIndexed { clusterIndex, cluster ->
        cluster.news.forEachIndexed { position, dto ->
            articles.add(dto.toArticleEntity())
            feedCache.add(dto.toFeedCacheEntity(feedType, clusterIndex, position))
        }
    }

    return articles to feedCache
}

// ── FeedArticleWithState → Domain ─────────────────────────────────────────────

fun FeedArticleWithState.toDomain(): NewsArticle = NewsArticle(
    id = article.id,
    title = article.title,
    text = article.text,
    summary = article.summary,
    url = article.url,
    image = article.image,
    video = article.video,
    publishDate = article.publishDate,
    authors = article.authors,
    category = article.category,
    language = article.language,
    sourceCountry = article.sourceCountry,
    sentiment = article.sentiment,
)

/**
 * Группируем плоский список FeedArticleWithState обратно в кластеры.
 * Порядок сохранён через clusterId + position (ORDER BY в запросе).
 */
fun List<FeedArticleWithState>.toClusters(): List<NewsCluster> {
    if (isEmpty()) return emptyList()

    val result = ArrayList<NewsCluster>()
    var currentClusterId = this[0].clusterId
    var buffer = ArrayList<NewsArticle>()

    for (row in this) {
        if (row.clusterId != currentClusterId) {
            result.add(NewsCluster(currentClusterId, buffer))
            currentClusterId = row.clusterId
            buffer = ArrayList()
        }
        buffer.add(row.toDomain())
    }
    result.add(NewsCluster(currentClusterId, buffer))
    return result
}

// ── ArticleEntity → Domain ────────────────────────────────────────────────────

fun ArticleEntity.toDomain(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    text = text,
    summary = summary,
    url = url,
    image = image,
    video = video,
    publishDate = publishDate,
    authors = authors,
    category = category,
    language = language,
    sourceCountry = sourceCountry,
    sentiment = sentiment,
)