package dev.alexmester.data.mapper

import dev.alexmester.data.remote.dto.NewsArticleDto
import dev.alexmester.data.remote.dto.NewsClusterDto
import dev.alexmester.database.dao.FeedArticleWithState
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster

internal fun NewsArticleDto.toDomain() = NewsArticle(
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

internal fun List<NewsClusterDto>.toDomain(): List<NewsCluster> =
    mapIndexedNotNull { index, cluster ->
        cluster.news
            .takeIf { it.isNotEmpty() }
            ?.let { NewsCluster(id = index, articles = it.map { dto -> dto.toDomain() }) }
    }

internal fun NewsArticle.toEntity() = ArticleEntity(
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

internal fun ArticleEntity.toDomain() = NewsArticle(
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

/** Список уже отсортирован DAO по clusterId, position; groupBy сохраняет порядок появления. */
internal fun List<FeedArticleWithState>.toClusters(): List<NewsCluster> =
    groupBy { it.clusterId }.map { (clusterId, rows) ->
        NewsCluster(id = clusterId, articles = rows.map { it.article.toDomain() })
    }

internal fun List<NewsCluster>.toEntities(
    feedType: String,
): Pair<List<ArticleEntity>, List<FeedCacheEntity>> {
    val now = System.currentTimeMillis()
    val articles = ArrayList<ArticleEntity>()
    val cache = ArrayList<FeedCacheEntity>()
    forEach { cluster ->
        cluster.articles.forEachIndexed { position, article ->
            articles += article.toEntity()
            cache += FeedCacheEntity(feedType, article.id, cluster.id, position, now)
        }
    }
    return articles to cache
}

internal fun List<NewsArticle>.toEntities(
    feedType: String,
    positionStart: Int,
): Pair<List<ArticleEntity>, List<FeedCacheEntity>> {
    val now = System.currentTimeMillis()
    val cache = mapIndexed { index, article ->
        FeedCacheEntity(feedType, article.id, 0, positionStart + index, now)
    }
    return map { it.toEntity() } to cache
}