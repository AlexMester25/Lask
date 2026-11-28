package dev.alexmester.impl.presentation.settings.mvi

sealed class SettingsIntent {
    data class SetTheme(val theme: AppTheme) : SettingsIntent()
    data object NavigateToLanguage : SettingsIntent()
    data object NavigateToCountry : SettingsIntent()
    data object NavigateToAutoTranslateLanguage : SettingsIntent()
    data object Back : SettingsIntent()
}

enum class AppTheme {
    SYSTEM, LIGHT, DARK;
}
