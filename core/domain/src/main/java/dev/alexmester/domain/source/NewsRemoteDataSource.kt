package dev.alexmester.domain.source

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.news.SearchParams
import dev.alexmester.models.result.AppResult

interface NewsRemoteDataSource {

    suspend fun getTopNews(
        country: String,
        language: String,
        maxNewsPerCluster: Int = 5,
    ): AppResult<List<NewsCluster>>

    suspend fun search(params: SearchParams): AppResult<List<NewsArticle>>
}