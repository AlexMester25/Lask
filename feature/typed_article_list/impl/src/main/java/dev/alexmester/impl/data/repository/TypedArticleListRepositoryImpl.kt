package dev.alexmester.impl.data.repository

import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.impl.domain.repository.TypedArticleListRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TypedArticleListRepositoryImpl(
    private val userStateDao: ArticleUserStateDao,
): TypedArticleListRepository {

    override fun observeReadArticles(): Flow<List<NewsArticle>> =
        userStateDao.observeReadArticles().map { entities -> entities.map { it.toDomain() } }

    override fun observeClappedArticles(): Flow<List<NewsArticle>> =
        userStateDao.observeClappedArticles().map { entities -> entities.map { it.toDomain() } }
}