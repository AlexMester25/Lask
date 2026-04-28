package dev.alexmester.impl.data.remote

import dev.alexmester.impl.data.remote.dto.SearchResponseDto
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.network.endpoints.ApiRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class SearchApiService(private val client: HttpClient) {
    suspend fun searchNews(
        query: String,
        filters: SearchFilters,
        offset: Int,
        number: Int,
    ): SearchResponseDto = client.get(ApiRoutes.News.SEARCH_NEWS) {
        parameter("text", query)
        filters.language?.let { parameter("language", it) }
        filters.country?.let { parameter("source-countries", it) }
        filters.category?.let { parameter("categories", it) }
        filters.earliestDate?.let { parameter("earliest-publish-date", it) }
        filters.latestDate?.let { parameter("latest-publish-date", it) }
        parameter("sort", "publish-time")
        parameter("sort-direction", if (filters.sortAscending) "ASC" else "DESC")
        parameter("offset", offset)
        parameter("number", number)
    }.body()
}