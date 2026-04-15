package dev.alexmester.impl.data.mapper

import dev.alexmester.database.dao.FeedArticleWithState
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.impl.data.remote.dto.ExploreNewsArticleDto
import dev.alexmester.models.news.NewsArticle
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun ExploreNewsArticleDto.toArticleEntity(): ArticleEntity = ArticleEntity(
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
)

fun ExploreNewsArticleDto.toFeedCacheEntity(
    feedType: String,
    position: Int,
): FeedCacheEntity = FeedCacheEntity(
    feedType = feedType,
    articleId = id,
    clusterId = 0,
    position = position,
    cachedAt = System.currentTimeMillis(),
)

fun List<ExploreNewsArticleDto>.toEntities(
    feedType: String,
    positionStart: Int,
): Pair<List<ArticleEntity>, List<FeedCacheEntity>> {
    val articles = mutableListOf<ArticleEntity>()
    val cache = mutableListOf<FeedCacheEntity>()

    forEachIndexed { index, article ->
        articles.add(article.toArticleEntity())
        cache.add(article.toFeedCacheEntity(feedType = feedType, position = positionStart + index))
    }

    return articles to cache
}

fun FeedArticleWithState.toDomain(): NewsArticle = NewsArticle(
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