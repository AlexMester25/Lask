package dev.alexmester.lask

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.BookmarksApi
import dev.alexmester.api.navigation.ExploreApi
import dev.alexmester.api.navigation.InterestsApi
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.api.navigation.NewsFeedRoute
import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.api.navigation.SearchApi
import dev.alexmester.api.navigation.TypedArticleListApi
import dev.alexmester.lask.app_bottom_navigation.AppBottomBar
import dev.alexmester.navigation.register
import dev.alexmester.ui.components.bottom_bar.LocalBottomBarHeight
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.shared_transition.SharedTransitionLocals
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.koin.compose.koinInject

private const val NAV_TRANSITION_MS = 300

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RootScreen(navController: NavHostController) {
    val newsFeedApi = koinInject<NewsFeedApi>()
    val bookmarkApi = koinInject<BookmarksApi>()
    val exploreApi = koinInject<ExploreApi>()
    val articleDetailApi = koinInject<ArticleDetailApi>()
    val profileApi = koinInject<ProfileApi>()
    val searchApi = koinInject<SearchApi>()
    val typedArticleListApi = koinInject<TypedArticleListApi>()
    val interestsApi = koinInject<InterestsApi>()

    val hazeState = rememberHazeState()
    val density = LocalDensity.current
    var bottomBarHeight by remember { mutableStateOf(0.dp) }

    SharedTransitionLayout {
        CompositionLocalProvider(
            SharedTransitionLocals.LocalSharedTransitionScope provides this,
            LocalBottomBarHeight provides bottomBarHeight,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = NewsFeedRoute,
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeSource(hazeState)
                        .background(MaterialTheme.LaskColors.backgroundPrimary),
                    enterTransition = { fadeIn(tween(NAV_TRANSITION_MS)) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { fadeOut(tween(NAV_TRANSITION_MS)) },
                ) {
                    register(newsFeedApi, navController)
                    register(exploreApi, navController)
                    register(bookmarkApi, navController)
                    register(articleDetailApi, navController)
                    register(profileApi, navController)
                    register(searchApi, navController)
                    register(typedArticleListApi, navController)
                    register(interestsApi, navController)
                }
                AppBottomBar(
                    navController = navController,
                    hazeState = hazeState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onSizeChanged { size ->
                            if (size.height > 0) {
                                bottomBarHeight = with(density) { size.height.toDp() }
                            }
                        },
                )
            }
        }
    }
}