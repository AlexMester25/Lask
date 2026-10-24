package dev.alexmester.lask.main

sealed interface MainUiState {
    data object Loading : MainUiState

    data class Ready(
        val isDarkTheme: Boolean?,        // null — следовать системе
        val isOnboardingCompleted: Boolean,
    ) : MainUiState
}