package dev.alexmester.data.repository

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

internal class UserPreferencesRepositoryImpl(
    private val UserPreferencesDataSource: UserPreferencesDataSource
) : UserPreferencesRepository {

    override fun observeUserPreferences(): Flow<UserPreferences> =
        UserPreferencesDataSource.userPreferences

    override suspend fun updateTheme(isDark: Boolean?) = UserPreferencesDataSource.updateTheme(isDark)

    override suspend fun updateLocaleManually(country: String, language: String) =
        UserPreferencesDataSource.updateLocaleManually(country, language)

    override suspend fun updateAutoTranslateLanguage(language: String) =
        UserPreferencesDataSource.updateAutoTranslateLanguage(language)

    override suspend fun updateProfile(name: String, avatarUri: String?) {
        UserPreferencesDataSource.updateAvatarUri(avatarUri)
        UserPreferencesDataSource.updateProfileName(name)
    }

    override suspend fun updateStreak() =
        UserPreferencesDataSource.updateStreak(LocalDate.now().toString())

    override suspend fun addInterest(keyword: String) = UserPreferencesDataSource.addInterest(keyword)

    override suspend fun removeInterest(keyword: String) = UserPreferencesDataSource.removeInterest(keyword)

    override suspend fun addXp(delta: Float) = UserPreferencesDataSource.addXp(delta)

    override suspend fun completeOnboarding() = UserPreferencesDataSource.completeOnboarding()

    override suspend fun initLocaleFromDevice(country: String, language: String) =
        UserPreferencesDataSource.initLocaleFromDevice(country, language)
}