package dev.alexmester.impl.domain.repository

import android.net.Uri
import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<Pair<UserPreferences, Int>>
    suspend fun applyEditChanges(imageUri: Uri?, name: String)
    suspend fun updateStreak()
    suspend fun updateTheme(isDark: Boolean?)
    suspend fun updateLocaleManually(country: String, language: String)
    suspend fun updateAutoTranslateLanguage(language: String)
}