package dev.alexmester.testing.fake

import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory [UserPreferencesRepository]: чтение отдаёт последнее эмитированное
 * значение, обновления меняют только состояние фейка.
 */
class FakeUserPreferencesRepository(
    initial: UserPreferences = UserPreferences(
        defaultCountry = "us",
        defaultLanguage = "en",
    ),
) : UserPreferencesRepository {

    private val _preferences = MutableStateFlow(initial)

    val preferences: UserPreferences
        get() = _preferences.value

    override fun observeUserPreferences(): Flow<UserPreferences> = _preferences.asStateFlow()

    fun emit(preferences: UserPreferences) {
        _preferences.value = preferences
    }

    override suspend fun updateTheme(isDark: Boolean?) {
        _preferences.update { it.copy(isDarkTheme = isDark) }
    }

    override suspend fun updateLocaleManually(country: String, language: String) {
        _preferences.update {
            it.copy(
                defaultCountry = country,
                defaultLanguage = language,
                isLocaleManuallySet = true,
            )
        }
    }

    override suspend fun updateAutoTranslateLanguage(language: String) {
        _preferences.update {
            it.copy(
                autoTranslateLanguage = language,
                isAutoTranslateManuallySet = true,
            )
        }
    }

    override suspend fun updateProfile(name: String, avatarUri: String?) {
        _preferences.update { it.copy(profileName = name, avatarUri = avatarUri) }
    }

    override suspend fun updateStreak() = Unit
    override suspend fun addInterest(keyword: String) = Unit
    override suspend fun removeInterest(keyword: String) = Unit
    override suspend fun addXp(delta: Float) = Unit

    override suspend fun completeOnboarding() {
        _preferences.update { it.copy(isOnboardingCompleted = true) }
    }

    override suspend fun initLocaleFromDevice(country: String, language: String) {
        _preferences.update {
            it.copy(
                defaultCountry = country,
                defaultLanguage = language,
            )
        }
    }
}
