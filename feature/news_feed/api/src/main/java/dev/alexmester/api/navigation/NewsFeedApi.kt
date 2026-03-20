package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

/**
 * Публичный контракт feature:news-feed.
 *
 * Другие фичи зависят только от этого интерфейса через :api модуль.
 * Реализация (NewsFeedImpl) живёт в :impl и регистрируется в Koin в :app.
 *
 * Пример использования в feature:explore:impl:
 * ```kotlin
 * class ExploreViewModel(
 *     private val newsFeedApi: NewsFeedApi
 * ) : ViewModel() {
 *
 *     fun onCountryClick(code: String, name: String) {
 *         navController.navigate(newsFeedApi.countryFeedRoute(code, name))
 *     }
 * }
 * ```
 */
interface NewsFeedApi : FeatureApi {

    /** Маршрут к главной ленте топ-новостей */
    fun feedRoute(): FeedRoute

    /** Маршрут к ленте новостей по стране */
    fun countryFeedRoute(countryCode: String, countryName: String): CountryFeedRoute

    /** Маршрут к ленте новостей по категории */
    fun categoryFeedRoute(category: String): CategoryFeedRoute
}