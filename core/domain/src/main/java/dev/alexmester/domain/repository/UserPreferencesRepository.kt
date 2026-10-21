package dev.alexmester.domain.repository

import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeUserPreferences(): Flow<UserPreferences>
    suspend fun updateTheme(isDark: Boolean?)
    suspend fun updateLocaleManually(country: String, language: String)
    suspend fun updateAutoTranslateLanguage(language: String)
    suspend fun updateProfile(name: String, avatarUri: String?)
    suspend fun updateStreak()
    suspend fun addInterest(keyword: String)
    suspend fun removeInterest(keyword: String)
    suspend fun addXp(delta: Float)
    suspend fun completeOnboarding()
    suspend fun initLocaleFromDevice(country: String, language: String)
}