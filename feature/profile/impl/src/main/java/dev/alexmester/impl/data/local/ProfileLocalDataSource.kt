package dev.alexmester.impl.data.local

import android.net.Uri
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class ProfileLocalDataSource(
    private val articleRepository: ArticleRepository,
    private val preferencesDataSource: UserPreferencesDataSource,
) {

    fun observeProfile(): Flow<Pair<UserPreferences, Int>> =
        preferencesDataSource.userPreferences
            .combine(articleRepository.observeReadCount()) { prefs, readCount ->
                prefs to readCount
            }

    suspend fun applyEditChanges(imageUri: Uri?, name: String) {
        preferencesDataSource.updateAvatarUri(imageUri)
        preferencesDataSource.updateProfileName(name)
    }

    suspend fun updateStreak() {
        val today = LocalDate.now().toString()
        preferencesDataSource.updateStreak(today)
    }

    suspend fun updateTheme(isDark: Boolean?) {
        preferencesDataSource.updateTheme(isDark)
    }

    suspend fun updateLocaleManually(country: String, language: String){
        preferencesDataSource.updateLocaleManually(country,language)
    }

    suspend fun updateAutoTranslateLanguage(language: String){
        preferencesDataSource.updateAutoTranslateLanguage(language)
    }

}