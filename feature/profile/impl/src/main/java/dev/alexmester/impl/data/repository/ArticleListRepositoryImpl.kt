package dev.alexmester.impl.data.repository

import dev.alexmester.impl.data.local.ArticleListLocalDataSource
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.impl.domain.repository.ArticleListRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleListRepositoryImpl(
    private val local: ArticleListLocalDataSource,
) : ArticleListRepository {

    override fun getReadArticles(): Flow<List<NewsArticle>> =
        local.observeReadArticles().map { entities -> entities.map { it.toDomain() } }

    override fun getClappedArticles(): Flow<List<NewsArticle>> =
        local.observeClappedArticles().map { entities -> entities.map { it.toDomain() } }
}