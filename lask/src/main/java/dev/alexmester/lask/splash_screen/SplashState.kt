package dev.alexmester.lask.splash_screen

sealed interface SplashState {
    data object Loading : SplashState
    data object Initializing : SplashState
    data class Ready(val isOnboardingCompleted: Boolean) : SplashState
}