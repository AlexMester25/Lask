package dev.alexmester.impl.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.InterestsApi
import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.api.navigation.ProfileRoute
import dev.alexmester.api.navigation.SettingsApi
import dev.alexmester.api.navigation.TypedArticleListApi
import dev.alexmester.impl.presentation.profile.ProfileScreen
import dev.alexmester.ui.shared_transition.SharedTransitionLocals

internal class ProfileImpl(
    private val typedArticleListApi: TypedArticleListApi,
    private val interestsApi: InterestsApi,
    private val settingsApi: SettingsApi,
) : ProfileApi {

    override fun profileRoute() = ProfileRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<ProfileRoute> {
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                ProfileScreen(
                    onNavigateToArticleList = { type ->
                        navController.navigate(
                            typedArticleListApi.typedArticleListRoute(type)
                        )
                    },
                    onNavigateToSystemSettings = {
                        navController.navigate(settingsApi.settingsRoute())
                    },
                    onNavigateToInterests = {
                        navController.navigate(interestsApi.interestsRoute())
                    }
                )
            }
        }
    }
}
