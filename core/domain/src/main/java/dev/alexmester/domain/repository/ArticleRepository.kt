package dev.alexmester.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun observeReadArticleIds(): Flow<List<Long>>
    fun observeReadArticles(): Flow<List<NewsArticle>>
    fun observeClappedArticles(): Flow<List<NewsArticle>>
    suspend fun saveArticles(articles: List<NewsArticle>)
}