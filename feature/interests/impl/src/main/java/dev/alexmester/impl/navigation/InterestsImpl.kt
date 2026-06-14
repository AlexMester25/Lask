package dev.alexmester.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.InterestsApi
import dev.alexmester.api.navigation.InterestsRoute
import dev.alexmester.impl.presentation.interests.InterestsScreen

class InterestsImpl: InterestsApi {

    override fun interestsRoute(): InterestsRoute = InterestsRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable<InterestsRoute> {
            InterestsScreen(
                onBack = { navController.navigateUp() }
            )
        }
    }
}