package dev.alexmester.data.repository

import dev.alexmester.data.mapper.toDomain
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleRepositoryImpl (
    private val userStateDao: ArticleUserStateDao,
): ArticleRepository {

    override fun observeReadArticleIds(): Flow<List<Long>> =
        userStateDao.observeReadArticleIds()

    override fun observeReadArticles(): Flow<List<NewsArticle>> =
        userStateDao.observeReadArticles().map { entities -> entities.map { it.toDomain() } }

    override fun observeClappedArticles(): Flow<List<NewsArticle>> =
        userStateDao.observeClappedArticles().map { entities -> entities.map { it.toDomain() } }
}