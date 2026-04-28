package dev.alexmester.impl.data.repository

import android.net.Uri
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.data.local.ProfileLocalDataSource
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.impl.domain.repository.ProfileRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val local: ProfileLocalDataSource,
) : ProfileRepository {

    override fun observeReadArticles(): Flow<List<NewsArticle>> =
        local.observeReadArticles().map { entities -> entities.map { it.toDomain() } }

    override fun observeClappedArticles(): Flow<List<NewsArticle>> =
        local.observeClappedArticles().map { entities -> entities.map { it.toDomain() } }

    override fun observeProfile(): Flow<Pair<UserPreferences, Int>> =
        local.observeProfile()

    override fun observeUserPreferences(): Flow<UserPreferences> =
        local.observeUserPreferences()

    override suspend fun applyEditChanges(imageUri: Uri?, name: String) =
        local.applyEditChanges(imageUri = imageUri, name = name)

    override suspend fun updateStreak() =
        local.updateStreak()

    override suspend fun updateTheme(isDark: Boolean?) =
        local.updateTheme(isDark)

    override suspend fun addInterest(keyWord: String) =
        local.addInterest(keyWord)

    override suspend fun removeInterest(keyWord: String) =
        local.removeInterest(keyWord)

    override suspend fun updateLocaleManually(country: String, language: String) =
        local.updateLocaleManually(country,language)

    override suspend fun updateAutoTranslateLanguage(language: String) =
        local.updateAutoTranslateLanguage(language)

}