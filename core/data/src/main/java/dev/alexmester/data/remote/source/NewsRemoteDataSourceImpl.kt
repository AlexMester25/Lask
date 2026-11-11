package dev.alexmester.data.remote.source

import dev.alexmester.data.error.NetworkErrorMapper
import dev.alexmester.data.extension.safeApiCall
import dev.alexmester.data.mapper.toDomain
import dev.alexmester.data.remote.api.WorldNewsApiService
import dev.alexmester.domain.source.NewsRemoteDataSource
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.news.SearchParams
import dev.alexmester.models.result.AppResult

internal class NewsRemoteDataSourceImpl(
    private val api: WorldNewsApiService,
) : NewsRemoteDataSource {

    private val errorMapper = NetworkErrorMapper.worldNews()

    override suspend fun getTopNews(
        country: String,
        language: String,
        maxNewsPerCluster: Int,
    ): AppResult<List<NewsCluster>> = safeApiCall(errorMapper) {
        api.getTopNews(country, language, maxNewsPerCluster).topNews.toDomain()
    }

    override suspend fun search(params: SearchParams): AppResult<List<NewsArticle>> =
        safeApiCall(errorMapper) {
            api.search(params).news.map { it.toDomain() }
        }
}