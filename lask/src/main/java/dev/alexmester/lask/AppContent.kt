package dev.alexmester.lask

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dev.alexmester.lask.main.MainUiState
import dev.alexmester.lask.main.MainViewModel
import dev.alexmester.lask.welcome_screen.WelcomeScreen
import dev.alexmester.ui.desing_system.LaskTheme

@Composable
fun AppContent(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ready = uiState as? MainUiState.Ready
    val darkTheme = ready?.isDarkTheme ?: isSystemInDarkTheme()

    SystemBarsEffect(darkTheme)
    LaskTheme(darkTheme = darkTheme) {
        if (ready != null) {
            Crossfade(
                targetState = ready.isOnboardingCompleted,
                label = "onboarding_gate",
            ) { isCompleted ->
                if (isCompleted) {
                    RootScreen(navController = rememberNavController())
                } else {
                    WelcomeScreen(onExploreClick = viewModel::completeOnboarding)
                }
            }
        }
    }
}