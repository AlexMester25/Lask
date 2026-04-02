package dev.alexmester.lask

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.ArticleDetailRoute
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.lask.app_bottom_navigation.AppBottomBar
import dev.alexmester.lask.welcome_screen.WelcomeRoute
import dev.alexmester.navigation.register
import dev.alexmester.lask.welcome_screen.welcomeScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.koin.compose.koinInject


@Composable
fun RootScreen(
    navController: NavHostController,
    startDestination: Any = WelcomeRoute,
    onOnboardingComplete: () -> Unit = {},
) {
    val newsFeedApi = koinInject<NewsFeedApi>()
    val articleDetailApi = koinInject<ArticleDetailApi>()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = remember(currentBackStackEntry) {
        val route = currentBackStackEntry?.destination?.route ?: return@remember false
        !route.contains(ArticleDetailRoute::class.qualifiedName!!) &&
        !route.contains(WelcomeRoute::class.qualifiedName!!)
    }

    val hazeState = rememberHazeState()

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    navController = navController,
                    hazeState = hazeState,
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.hazeSource(hazeState),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            welcomeScreen(navController, onOnboardingComplete)
            register(newsFeedApi, navController)
            register(articleDetailApi, navController)
        }
    }
}