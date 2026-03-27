package dev.alexmester.lask

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.alexmester.api.navigation.BookMarkRoute
import dev.alexmester.api.navigation.ExploreRoute
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.api.navigation.ProfileRoute
import dev.alexmester.lask.navigation.WelcomeRoute
import dev.alexmester.navigation.register
import dev.alexmester.ui.R
import dev.alexmester.ui.components.bottom_bar.BottomBarItem
import dev.alexmester.ui.components.bottom_bar.BottomTab
import dev.alexmester.ui.components.bottom_bar.LaskBottomBar
import dev.alexmester.ui.components.welcome_screen.WelcomeScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.koin.compose.koinInject


@Composable
fun RootScreen(
    navController: NavHostController
) {
    val newsFeedApi = koinInject<NewsFeedApi>()

    val tabs = listOf(
        BottomTab(
            icon = ImageVector.vectorResource(R.drawable.ic_trends),
            title = stringResource(R.string.tab_top_news),
            route = FeedRoute
        ),
        BottomTab(
            icon = ImageVector.vectorResource(R.drawable.ic_explore),
            title = stringResource(R.string.tab_explore),
            route = ExploreRoute
        ),
        BottomTab(
            icon = ImageVector.vectorResource(R.drawable.ic_bookmark),
            title = stringResource(R.string.tab_bookmark),
            route = BookMarkRoute
        ),
        BottomTab(
            icon = ImageVector.vectorResource(R.drawable.ic_profile),
            title = stringResource(R.string.tab_profile),
            route = ProfileRoute
        )
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute != WelcomeRoute::class.qualifiedName
    var selectedTab by remember {
        mutableStateOf(tabs.first().route)
    }

    val hazeState = rememberHazeState()

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            if (showBottomBar){
                LaskBottomBar(
                    modifier = Modifier,
                    hazeState = hazeState,
                    items = tabs.map { tab ->
                        BottomBarItem(
                            icon = tab.icon,
                            title = tab.title,
                            isSelected = tab.route == selectedTab,
                            onClick = {
                                if (tab.route == selectedTab) return@BottomBarItem
                                selectedTab = tab.route
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
//                                restoreState = true
                                }
                            }
                        )
                    }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = WelcomeRoute,
            modifier = Modifier.hazeSource(hazeState),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            register(newsFeedApi, navController)
            composable<WelcomeRoute> {
                WelcomeScreen(
                    onExploreClick = {
                        navController.navigate(FeedRoute) {
                            popUpTo(WelcomeRoute) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}