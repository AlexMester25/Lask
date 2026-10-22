package dev.alexmester.lask.splash_screen

sealed interface SplashState {
    data object Loading : SplashState
    data class Ready(val isOnboardingCompleted: Boolean) : SplashState
}