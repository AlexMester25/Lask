package dev.alexmester.impl.data.local

import android.net.Uri
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class ProfileLocalDataSource(
    private val userStateDao: ArticleUserStateDao,
    private val preferencesDataSource: UserPreferencesDataSource,
) {
    fun observeReadArticles(): Flow<List<ArticleEntity>> =
        userStateDao.observeReadArticles()

    fun observeClappedArticles(): Flow<List<ArticleEntity>> =
        userStateDao.observeClappedArticles()

    fun observeProfile(): Flow<Pair<UserPreferences, Int>> =
        preferencesDataSource.userPreferences
            .combine(
                userStateDao.observeReadCount()
            ) { prefs, readCount ->
                prefs to readCount
            }


    fun observeUserPreferences(): Flow<UserPreferences> =
        preferencesDataSource.userPreferences

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

    suspend fun addInterest(keyWord: String){
        preferencesDataSource.addInterest(keyWord)
    }

    suspend fun removeInterest(keyWord: String){
        preferencesDataSource.removeInterest(keyWord)
    }

    suspend fun updateLocaleManually(country: String, language: String){
        preferencesDataSource.updateLocaleManually(country,language)
    }

    suspend fun updateAutoTranslateLanguage(language: String){
        preferencesDataSource.updateAutoTranslateLanguage(language)
    }

}