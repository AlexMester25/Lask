package dev.alexmester.impl.data.mapper

import dev.alexmester.database.entity.BookmarkEntity
import dev.alexmester.models.news.NewsArticle
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun BookmarkEntity.toDomain(): NewsArticle = NewsArticle(
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