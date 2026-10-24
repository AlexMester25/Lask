package dev.alexmester.lask.app_bottom_navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.alexmester.ui.components.bottom_bar.LaskBottomBar
import dev.alexmester.ui.components.bottom_bar.LaskMainBottomBarItem
import dev.chrisbanes.haze.HazeState

@Composable
fun AppBottomBar(
    navController: NavHostController,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val tabs = remember { AppTabs.getTabs() }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = currentBackStackEntry?.destination

    if (!destination.shouldShowBottomBar(tabs)) return

    LaskBottomBar(
        modifier = modifier,
        hazeState = hazeState,
        items = tabs.map { tab ->
            val isSelected = destination.isInHierarchyOf(tab)
            LaskMainBottomBarItem(
                icon = ImageVector.vectorResource(tab.iconRes),
                title = stringResource(tab.titleRes),
                isSelected = isSelected,
                onClick = {
                    if (isSelected) return@LaskMainBottomBarItem
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    )

}

private fun NavDestination?.isInHierarchyOf(tab: AppBottomTab): Boolean =
    this?.hierarchy?.any { it.hasRoute(tab.route::class) } == true

private fun NavDestination?.shouldShowBottomBar(tabs: List<AppBottomTab>): Boolean =
    tabs.any { isInHierarchyOf(it) }