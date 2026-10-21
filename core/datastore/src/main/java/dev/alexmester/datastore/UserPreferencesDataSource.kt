package dev.alexmester.datastore

import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {

    val userPreferences: Flow<UserPreferences>

    suspend fun completeOnboarding()

    suspend fun initLocaleFromDevice(country: String, language: String)

    suspend fun updateLocaleManually(country: String, language: String)

    suspend fun updateAutoTranslateLanguage(language: String)

    suspend fun updateTheme(isDark: Boolean?)

    suspend fun updateProfileName(name: String)

    suspend fun updateAvatarUri(uri: String?)

    suspend fun updateStreak(today: String)

    suspend fun addInterest(keyword: String)

    suspend fun removeInterest(keyword: String)

    suspend fun addXp(xpDelta: Float)
}