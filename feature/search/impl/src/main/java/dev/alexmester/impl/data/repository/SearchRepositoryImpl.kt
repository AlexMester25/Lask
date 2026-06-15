package dev.alexmester.impl.data.repository

import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.impl.data.mapper.toEntity
import dev.alexmester.impl.data.remote.SearchApiService
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.impl.domain.repository.SearchRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.error.WorldNewsErrorMapper
import dev.alexmester.network.extension.safeApiCall

class SearchRepositoryImpl(
    private val remote: SearchApiService,
    private val articleDao: ArticleDao,
) : SearchRepository {

    private val errorMapper = WorldNewsErrorMapper()

    override suspend fun search(
        query: String,
        filters: SearchFilters,
        offset: Int,
        number: Int,
    ): AppResult<List<NewsArticle>> = safeApiCall(errorMapper) {
        val dtos = remote.searchNews(
            query = query,
            filters = filters,
            offset = offset,
            number = number,
        ).news

        articleDao.insertArticles(dtos.map { it.toEntity() })
        dtos.map { it.toDomain() }
    }
}