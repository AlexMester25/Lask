package dev.alexmester.impl.data.repository

import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.source.NewsRemoteDataSource
import dev.alexmester.impl.data.mapper.toSearchParams
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.impl.domain.repository.SearchRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.result.runAppResult

class SearchRepositoryImpl(
    private val remote: NewsRemoteDataSource,
    private val articleRepository: ArticleRepository,
) : SearchRepository {

    override suspend fun search(
        query: String,
        filters: SearchFilters,
        offset: Int,
        number: Int,
    ): AppResult<List<NewsArticle>> =
        when (val result = remote.search(filters.toSearchParams(query, offset, number))) {
            is AppResult.Failure -> result
            is AppResult.Success -> runAppResult {
                articleRepository.saveArticles(result.data)
                result.data
            }
        }
}