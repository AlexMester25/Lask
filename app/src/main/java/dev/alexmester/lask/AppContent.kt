package dev.alexmester.lask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.lask.welcome_screen.SplashState
import dev.alexmester.lask.welcome_screen.SplashViewModel
import dev.alexmester.lask.welcome_screen.WelcomeRoute
import dev.alexmester.ui.desing_system.LaskTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppContent(
    splashViewModel: SplashViewModel,
) {
    val splashState by splashViewModel.state.collectAsState()

    LaskTheme {
        when (val state = splashState) {
            SplashState.Loading,
            SplashState.Initializing -> Unit

            is SplashState.Ready -> {
                val navController = rememberNavController()
                val startDestination = remember {
                    if (state.isOnboardingCompleted) FeedRoute else WelcomeRoute
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