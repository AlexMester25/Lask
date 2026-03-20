package dev.alexmester.api.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe маршруты feature:news-feed.
 * Живут в :api — другие фичи получают их через NewsFeedApi методы.
 */
@Serializable
data object FeedRoute

@Serializable
data class CountryFeedRoute(
    val countryCode: String,
    val countryName: String,
)

@Serializable
data class CategoryFeedRoute(
    val category: String,
)