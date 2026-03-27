package dev.alexmester.impl.domain.interactor

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NewsFeedInteractor(
    private val repository: NewsFeedRepository,
    private val preferencesDataSource: UserPreferencesDataSource,
) {

    fun getClustersFlow(): Flow<List<NewsCluster>> =
        repository.getClustersFlow()

    suspend fun refresh(forceRefresh: Boolean = false): AppResult<Unit> {
        val prefs = preferencesDataSource.userPreferences.first()
        return repository.refreshTopNews(
            country = prefs.defaultCountry,
            language = prefs.defaultLanguage,
            forceRefresh = forceRefresh,
        )
    }

    suspend fun getLastCachedAt(): Long? =
        repository.getLastCachedAt()

    suspend fun getCountry(): String {
        return preferencesDataSource.userPreferences.first().defaultCountry
    }
}
