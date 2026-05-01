package dev.alexmester.impl.data.remote

import dev.alexmester.impl.data.remote.dto.TopNewsResponseDto
import dev.alexmester.network.endpoints.ApiRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.LocalDate
import java.time.ZoneOffset


class NewsFeedApiService(private val client: HttpClient) {

    suspend fun getTopNews(
        sourceCountry: String,
        language: String,
        maxNewsPerCluster: Int = 5,
    ): TopNewsResponseDto {
        val today = LocalDate.now(ZoneOffset.UTC).toString()

        val todayResponse = fetchTopNews(
            sourceCountry = sourceCountry,
            language = language,
            date = today,
            maxNewsPerCluster = maxNewsPerCluster,
        )

        if (todayResponse.topNews.isNotEmpty()) return todayResponse

        val yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1).toString()
        return fetchTopNews(
            sourceCountry = sourceCountry,
            language = language,
            date = yesterday,
            maxNewsPerCluster = maxNewsPerCluster,
        )
    }

    private suspend fun fetchTopNews(
        sourceCountry: String,
        language: String,
        date: String,
        maxNewsPerCluster: Int,
    ): TopNewsResponseDto = client.get(ApiRoutes.News.TOP_NEWS) {
        parameter("source-country", sourceCountry)
        parameter("language", language)
        parameter("date", date)
        parameter("headlines-only", false)
        parameter("max-news-per-cluster", maxNewsPerCluster)
    }.body()
}