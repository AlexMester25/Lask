package dev.alexmester.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * Extension для удобной регистрации FeatureApi в NavHost.
 *
 * Использование в :app:
 * ```kotlin
 * NavHost(...) {
 *     register(get<NewsFeedApi>(), navController)
 *     register(get<ExploreApi>(), navController)
 *     register(get<BookmarksApi>(), navController)
 * }
 * ```
 */
fun NavGraphBuilder.register(
    featureApi: FeatureApi,
    navController: NavHostController,
) {
    featureApi.registerGraph(
        navGraphBuilder = this,
        navController = navController,
    )
}