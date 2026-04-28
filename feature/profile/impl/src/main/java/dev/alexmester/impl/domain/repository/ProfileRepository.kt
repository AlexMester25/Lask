package dev.alexmester.impl.domain.repository

import android.net.Uri
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeReadArticles(): Flow<List<NewsArticle>>
    fun observeClappedArticles(): Flow<List<NewsArticle>>
    fun observeProfile(): Flow<Pair<UserPreferences, Int>>
    fun observeUserPreferences(): Flow<UserPreferences>
    suspend fun applyEditChanges(imageUri: Uri?, name: String)
    suspend fun updateStreak()
    suspend fun updateTheme(isDark: Boolean?)
    suspend fun addInterest(keyWord: String)
    suspend fun removeInterest(keyWord: String)
    suspend fun updateLocaleManually(country: String, language: String)
    suspend fun updateAutoTranslateLanguage(language: String)
}