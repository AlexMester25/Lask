package dev.alexmester.impl.data.repository

import android.net.Uri
import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.impl.data.local.ProfileLocalDataSource
import dev.alexmester.impl.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val local: ProfileLocalDataSource,
) : ProfileRepository {

    override fun observeProfile(): Flow<Pair<UserPreferences, Int>> =
        local.observeProfile()

    override suspend fun applyEditChanges(imageUri: Uri?, name: String) =
        local.applyEditChanges(imageUri = imageUri, name = name)

    override suspend fun updateStreak() =
        local.updateStreak()

    override suspend fun updateTheme(isDark: Boolean?) =
        local.updateTheme(isDark)

    override suspend fun updateLocaleManually(country: String, language: String) =
        local.updateLocaleManually(country,language)

    override suspend fun updateAutoTranslateLanguage(language: String) =
        local.updateAutoTranslateLanguage(language)

}