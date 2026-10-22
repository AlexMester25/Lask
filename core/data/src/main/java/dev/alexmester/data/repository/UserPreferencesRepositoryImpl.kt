package dev.alexmester.data.repository

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.LocalDate

internal class UserPreferencesRepositoryImpl(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val clock: Clock,
) : UserPreferencesRepository {

    override fun observeUserPreferences(): Flow<UserPreferences> =
        userPreferencesDataSource.userPreferences

    override suspend fun updateTheme(isDark: Boolean?) = userPreferencesDataSource.updateTheme(isDark)

    override suspend fun updateLocaleManually(country: String, language: String) =
        userPreferencesDataSource.updateLocaleManually(country, language)

    override suspend fun updateAutoTranslateLanguage(language: String) =
        userPreferencesDataSource.updateAutoTranslateLanguage(language)

    override suspend fun updateProfile(name: String, avatarUri: String?) {
        userPreferencesDataSource.updateAvatarUri(avatarUri)
        userPreferencesDataSource.updateProfileName(name)
    }

    override suspend fun updateStreak() =
        userPreferencesDataSource.updateStreak(LocalDate.now(clock).toString())

    override suspend fun addInterest(keyword: String) = userPreferencesDataSource.addInterest(keyword)

    override suspend fun removeInterest(keyword: String) = userPreferencesDataSource.removeInterest(keyword)

    override suspend fun addXp(delta: Float) = userPreferencesDataSource.addXp(delta)

    override suspend fun completeOnboarding() = userPreferencesDataSource.completeOnboarding()

    override suspend fun initLocaleFromDevice(country: String, language: String) =
        userPreferencesDataSource.initLocaleFromDevice(country, language)
}