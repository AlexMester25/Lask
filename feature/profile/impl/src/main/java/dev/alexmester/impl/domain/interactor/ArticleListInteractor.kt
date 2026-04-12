package dev.alexmester.impl.domain.interactor

import dev.alexmester.impl.domain.repository.ArticleListRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ArticleListInteractor(
    private val repository: ArticleListRepository,
) {
    fun getReadArticles(): Flow<List<NewsArticle>> = repository.getReadArticles()
    fun getClappedArticles(): Flow<List<NewsArticle>> = repository.getClappedArticles()
}