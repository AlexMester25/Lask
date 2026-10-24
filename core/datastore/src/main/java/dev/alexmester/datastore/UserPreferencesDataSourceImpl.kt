package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_AUTO_TRANSLATE_LANGUAGE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_AUTO_TRANSLATE_MANUALLY_SET
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_AVATAR_URI
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_CURRENT_LEVEL
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_CURRENT_XP
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_DEFAULT_COUNTRY
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_DEFAULT_LANGUAGE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_INTERESTS
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_IS_DARK_THEME
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_IS_THEME_SET
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_LAST_STREAK_DATE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_LOCALE_MANUALLY_SET
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_ONBOARDING_DONE
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_PROFILE_NAME
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_STREAK_COUNT
import dev.alexmester.domain.interests.InterestRules
import dev.alexmester.domain.progress.Progress
import dev.alexmester.domain.progress.Streak
import dev.alexmester.models.locale.SupportedLocales
import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.utils.constants.LaskConstants.ANONIM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesDataSource {

    override val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            UserPreferences(
                defaultCountry = prefs[KEY_DEFAULT_COUNTRY] ?: SupportedLocales.FALLBACK_COUNTRY,
                defaultLanguage = prefs[KEY_DEFAULT_LANGUAGE] ?: SupportedLocales.FALLBACK_LANGUAGE,
                isDarkTheme = if (prefs[KEY_IS_THEME_SET] == true) prefs[KEY_IS_DARK_THEME] else null,
                isOnboardingCompleted = prefs[KEY_ONBOARDING_DONE] ?: false,
                isLocaleManuallySet = prefs[KEY_LOCALE_MANUALLY_SET] ?: false,
                profileName = prefs[KEY_PROFILE_NAME] ?: ANONIM,
                avatarUri = prefs[KEY_AVATAR_URI],
                streakCount = prefs[KEY_STREAK_COUNT] ?: 0,
                lastStreakDate = prefs[KEY_LAST_STREAK_DATE],
                currentXp = prefs[KEY_CURRENT_XP] ?: 0f,
                currentLevel = prefs[KEY_CURRENT_LEVEL] ?: 1,
                interests = prefs[KEY_INTERESTS] ?: emptySet(),
                autoTranslateLanguage = prefs[KEY_AUTO_TRANSLATE_LANGUAGE]
                    ?: SupportedLocales.FALLBACK_LANGUAGE,
                isAutoTranslateManuallySet = prefs[KEY_AUTO_TRANSLATE_MANUALLY_SET] ?: false,
            )
        }

    override suspend fun completeOnboarding() {
        dataStore.edit { it[KEY_ONBOARDING_DONE] = true }
    }

    override suspend fun initLocaleFromDevice(country: String, language: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_COUNTRY] = country
            prefs[KEY_DEFAULT_LANGUAGE] = language
            prefs[KEY_LOCALE_MANUALLY_SET] = false
            if (prefs[KEY_AUTO_TRANSLATE_MANUALLY_SET] != true) {
                prefs[KEY_AUTO_TRANSLATE_LANGUAGE] = language
            }
        }
    }

    override suspend fun updateLocaleManually(country: String, language: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_COUNTRY] = country
            prefs[KEY_DEFAULT_LANGUAGE] = language
            prefs[KEY_LOCALE_MANUALLY_SET] = true
        }
    }

    override suspend fun updateAutoTranslateLanguage(language: String) {
        dataStore.edit { prefs ->
            prefs[KEY_AUTO_TRANSLATE_LANGUAGE] = language
            prefs[KEY_AUTO_TRANSLATE_MANUALLY_SET] = true
        }
    }

    override suspend fun updateTheme(isDark: Boolean?) {
        dataStore.edit { prefs ->
            if (isDark == null) {
                prefs.remove(KEY_IS_DARK_THEME)
                prefs[KEY_IS_THEME_SET] = false
            } else {
                prefs[KEY_IS_DARK_THEME] = isDark
                prefs[KEY_IS_THEME_SET] = true
            }
        }
    }

    override suspend fun updateProfileName(name: String) {
        dataStore.edit { it[KEY_PROFILE_NAME] = name.trim().ifEmpty { ANONIM } }
    }

    override suspend fun updateAvatarUri(uri: String?) {
        dataStore.edit { prefs ->
            if (uri == null) prefs.remove(KEY_AVATAR_URI)
            else prefs[KEY_AVATAR_URI] = uri
        }
    }

    override suspend fun updateStreak(today: String) {
        dataStore.edit { prefs ->
            val next = Streak(
                count = prefs[KEY_STREAK_COUNT] ?: 0,
                lastDate = prefs[KEY_LAST_STREAK_DATE],
            ).record(today)

            prefs[KEY_STREAK_COUNT] = next.count
            prefs[KEY_LAST_STREAK_DATE] = today
        }
    }

    override suspend fun addInterest(keyword: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_INTERESTS] ?: emptySet()
            if (current.size >= InterestRules.MAX_COUNT) return@edit
            if (InterestRules.validate(keyword, current) != null) return@edit
            prefs[KEY_INTERESTS] = current + InterestRules.normalize(keyword)
        }
    }

    override suspend fun removeInterest(keyword: String) {
        val normalized = InterestRules.normalize(keyword)
        dataStore.edit { prefs ->
            val current = prefs[KEY_INTERESTS] ?: emptySet()
            if (normalized in current) prefs[KEY_INTERESTS] = current - normalized
        }
    }

    override suspend fun addXp(xpDelta: Float) {
        dataStore.edit { prefs ->
            val next = Progress(
                level = prefs[KEY_CURRENT_LEVEL] ?: Progress.MIN_LEVEL,
                xp = prefs[KEY_CURRENT_XP] ?: 0f,
            ).addXp(xpDelta)

            prefs[KEY_CURRENT_XP] = next.xp
            prefs[KEY_CURRENT_LEVEL] = next.level
        }
    }
}