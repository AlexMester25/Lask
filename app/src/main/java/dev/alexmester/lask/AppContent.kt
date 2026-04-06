package dev.alexmester.lask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dev.alexmester.api.navigation.NewsFeedRoute
import dev.alexmester.lask.welcome_screen.SplashState
import dev.alexmester.lask.welcome_screen.SplashViewModel
import dev.alexmester.lask.welcome_screen.WelcomeRoute
import dev.alexmester.ui.desing_system.LaskTheme

@Composable
fun AppContent(
    splashViewModel: SplashViewModel,
) {
    val splashState by splashViewModel.state.collectAsStateWithLifecycle()

    LaskTheme {
        when (val state = splashState) {
            SplashState.Loading,
            SplashState.Initializing -> Unit

            is SplashState.Ready -> {
                val navController = rememberNavController()
                val startDestination = remember {
                    if (state.isOnboardingCompleted) NewsFeedRoute else WelcomeRoute
                }
                RootScreen(
                    navController = navController,
                    startDestination = startDestination,
                    onOnboardingComplete = {
                        splashViewModel.completeOnboarding()
                    },
                )
            }
        }
    }
}