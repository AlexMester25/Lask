package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface TypedArticleListRepository {
    fun observeReadArticles(): Flow<List<NewsArticle>>
    fun observeClappedArticles(): Flow<List<NewsArticle>>
}