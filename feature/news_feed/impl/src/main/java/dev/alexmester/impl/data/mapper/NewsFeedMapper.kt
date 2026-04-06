package dev.alexmester.impl.data.mapper

import dev.alexmester.database.entity.NewsArticleEntity
import dev.alexmester.impl.data.remote.dto.NewsArticleDto
import dev.alexmester.impl.data.remote.dto.NewsClusterDto
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// ── DTO → Entity ──────────────────────────────────────────────────────────────

fun NewsArticleDto.toEntity(sourceScreen: String, clusterId: Int): NewsArticleEntity =
    NewsArticleEntity(
        id = id,
        title = title,
        text = text,
        summary = summary,
        url = url,
        image = image,
        video = video,
        publishDate = publishDate,
        authors = json.encodeToString(authors),
        category = category,
        language = language,
        sourceCountry = sourceCountry,
        sentiment = sentiment,
        cachedAt = System.currentTimeMillis(),
        sourceScreen = sourceScreen,
        clusterId = clusterId,
    )

fun List<NewsClusterDto>.dtosToEntities(sourceScreen: String): List<NewsArticleEntity> =
    flatMapIndexed { index, cluster ->
        cluster.news.map { dto -> dto.toEntity(sourceScreen, clusterId = index) }
    }

// ── DTO → Domain ──────────────────────────────────────────────────────────────

fun NewsArticleDto.toDomain(): NewsArticle = NewsArticle(
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

fun NewsClusterDto.toDomain(id: Int): NewsCluster = NewsCluster(
    id = id,
    articles = news.map { it.toDomain() },
)

fun List<NewsClusterDto>.dtosToDomain(): List<NewsCluster> =
    mapIndexed { index, dto -> dto.toDomain(index) }
        .filter { it.articles.isNotEmpty() }

// ── Domain → Entity ───────────────────────────────────────────────────────────

fun NewsArticle.toEntity(sourceScreen: String, clusterId: Int): NewsArticleEntity = NewsArticleEntity(
    id = id,
    title = title,
    text = text,
    summary = summary,
    url = url,
    image = image,
    video = video,
    publishDate = publishDate,
    authors = json.encodeToString(authors),
    category = category,
    language = language,
    sourceCountry = sourceCountry,
    sentiment = sentiment,
    cachedAt = System.currentTimeMillis(),
    sourceScreen = sourceScreen,
    clusterId = clusterId,
)

fun List<NewsCluster>.toEntities(sourceScreen: String): List<NewsArticleEntity> =
    flatMap { cluster ->
        cluster.articles.map { article ->
            article.toEntity(sourceScreen, clusterId = cluster.id)
        }
    }

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun NewsArticleEntity.toDomain(): NewsArticle = NewsArticle(
    id = id,
    title = title,
    text = text,
    summary = summary,
    url = url,
    image = image,
    video = video,
    publishDate = publishDate,
    authors = json.decodeFromString(authors),
    category = category,
    language = language,
    sourceCountry = sourceCountry,
    sentiment = sentiment,
)

fun List<NewsArticleEntity>.entitiesToClusters(): List<NewsCluster> =
    groupBy { it.clusterId }
        .entries
        .sortedBy { it.key }
        .map { (clusterId, entities) ->
            NewsCluster(
                id = clusterId,
                articles = entities.map { it.toDomain() },
            )
        }