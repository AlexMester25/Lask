package dev.alexmester.datastore.model

import dev.alexmester.models.news.SupportedLocales

data class UserPreferences(
    val defaultCountry: String = SupportedLocales.FALLBACK_COUNTRY,
    val defaultLanguage: String = SupportedLocales.FALLBACK_LANGUAGE,
    val isDarkTheme: Boolean? = null,
    val isOnboardingCompleted: Boolean = false,
    val isLocaleManuallySet: Boolean = false,
)