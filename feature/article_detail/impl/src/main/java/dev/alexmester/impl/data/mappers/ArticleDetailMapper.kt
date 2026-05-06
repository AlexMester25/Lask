package dev.alexmester.impl.data.mappers

import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.models.news.NewsArticle

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