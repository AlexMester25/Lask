package dev.alexmester.testing.builder

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster

/**
 * Собирает [NewsArticle] с предсказуемыми значениями по умолчанию, чтобы тесты
 * указывали только значимые для сценария поля.
 */
fun buildArticle(
    id: Long = 1L,
    title: String = "Title $id",
    text: String? = null,
    summary: String? = null,
    url: String = "https://example.com/$id",
    image: String? = null,
    video: String? = null,
    publishDate: String = "2026-04-26",
    authors: List<String> = emptyList(),
    category: String? = null,
    language: String? = "en",
    sourceCountry: String? = "us",
    sentiment: Double? = null,
): NewsArticle = NewsArticle(
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

/** Собирает [NewsCluster] из [articleCount] статей с производными id. */
fun buildCluster(
    id: Int,
    articleCount: Int = 2,
): NewsCluster = NewsCluster(
    id = id,
    articles = (1..articleCount).map { index -> buildArticle(id = id.toLong() * 10 + index) },
)
