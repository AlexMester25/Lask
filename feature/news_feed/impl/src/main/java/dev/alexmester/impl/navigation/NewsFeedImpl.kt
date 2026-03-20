package dev.alexmester.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.alexmester.api.navigation.CategoryFeedRoute
import dev.alexmester.api.navigation.CountryFeedRoute
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.api.navigation.NewsFeedApi
//import dev.alexmester.newsfeed.impl.presentation.screen.CategoryFeedScreen
//import dev.alexmester.newsfeed.impl.presentation.screen.CountryFeedScreen
//import dev.alexmester.newsfeed.impl.presentation.screen.NewsFeedScreen

/**
 * Реализация NewsFeedApi.
 * Знает только о своих экранах.
 * Регистрируется в Koin в :app:
 * ```kotlin
 * single<NewsFeedApi> { NewsFeedImpl() }
 * ```
 */
class NewsFeedImpl : NewsFeedApi {

    override fun feedRoute() = FeedRoute

    override fun countryFeedRoute(
        countryCode: String,
        countryName: String
    ) = CountryFeedRoute(countryCode = countryCode, countryName = countryName)

    override fun categoryFeedRoute(category: String) = CategoryFeedRoute(category = category)

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<FeedRoute> {
//            NewsFeedScreen(navController = navController)
        }

        navGraphBuilder.composable<CountryFeedRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CountryFeedRoute>()
//            CountryFeedScreen(
//                countryCode = route.countryCode,
//                countryName = route.countryName,
//                navController = navController,
//            )
        }

        navGraphBuilder.composable<CategoryFeedRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CategoryFeedRoute>()
//            CategoryFeedScreen(
//                category = route.category,
//                navController = navController,
//            )
        }
    }
}