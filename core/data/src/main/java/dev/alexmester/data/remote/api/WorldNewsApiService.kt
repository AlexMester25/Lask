package dev.alexmester.data.remote.api

import dev.alexmester.data.remote.dto.SearchNewsResponseDto
import dev.alexmester.data.remote.dto.TopNewsResponseDto
import dev.alexmester.models.news.SearchParams
import dev.alexmester.network.endpoints.ApiRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

internal class WorldNewsApiService(
    private val client: HttpClient,
    private val clock: Clock,
) {

    suspend fun getTopNews(
        country: String,
        language: String,
        maxNewsPerCluster: Int,
    ): TopNewsResponseDto {
        val today = LocalDate.now(clock.withZone(ZoneOffset.UTC))
        val todayResponse = fetchTopNews(country, language, today.toString(), maxNewsPerCluster)
        if (todayResponse.topNews.isNotEmpty()) return todayResponse
        return fetchTopNews(country, language, today.minusDays(1).toString(), maxNewsPerCluster)
    }

    private suspend fun fetchTopNews(
        country: String,
        language: String,
        date: String,
        maxNewsPerCluster: Int,
    ): TopNewsResponseDto = client.get(ApiRoutes.News.TOP_NEWS) {
        parameter("source-country", country)
        parameter("language", language)
        parameter("date", date)
        parameter("headlines-only", false)
        parameter("max-news-per-cluster", maxNewsPerCluster)
    }.body()

    suspend fun search(params: SearchParams): SearchNewsResponseDto =
        client.get(ApiRoutes.News.SEARCH_NEWS) {
            parameter("text", params.text)
            parameter("text-match-indexes", params.textMatchIndexes.csv())
            parameter("language", params.language)
            parameter("source-countries", params.sourceCountries.csv())
            parameter("categories", params.categories.csv())
            parameter("min-sentiment", params.minSentiment)
            parameter("max-sentiment", params.maxSentiment)
            parameter("earliest-publish-date", params.earliestPublishDate)
            parameter("latest-publish-date", params.latestPublishDate)
            parameter("authors", params.authors.csv())
            parameter("news-sources", params.newsSources.csv())
            parameter("location-filter", params.locationFilter)
            parameter("sort", params.sort?.apiValue)
            parameter("sort-direction", params.sortDirection?.apiValue)
            parameter("offset", params.offset)
            parameter("number", params.number)
        }.body()

    private fun List<String>?.csv(): String? =
        this?.takeIf { it.isNotEmpty() }?.joinToString(",")
}