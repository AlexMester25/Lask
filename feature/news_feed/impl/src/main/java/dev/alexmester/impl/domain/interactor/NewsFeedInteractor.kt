package dev.alexmester.impl.domain.interactor

import android.util.Log
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NewsFeedInteractor(
    private val repository: NewsFeedRepository,
    private val preferencesDataSource: UserPreferencesDataSource,
) {
    private val refreshMutex = Mutex()

    fun getClustersWithPrefsFlow(): Flow<Pair<List<NewsCluster>, UserPreferences>> =
        repository.getClustersFlow()
            .combine(
                preferencesDataSource.userPreferences
                    .drop(1)
                    .distinctUntilChanged { old, new ->
                        old.defaultCountry == new.defaultCountry &&
                        old.defaultLanguage == new.defaultLanguage
                    }
            ) { clusters, prefs ->
                clusters to prefs
            }

    fun getClustersFlow(): Flow<List<NewsCluster>> =
        repository.getClustersFlow()

    fun getPreferencesFlow(): Flow<UserPreferences> =
        preferencesDataSource.userPreferences

    suspend fun refresh(forceRefresh: Boolean = false): AppResult<Unit> {
        if (refreshMutex.isLocked) return AppResult.Success(Unit)
        return refreshMutex.withLock {
            val prefs = preferencesDataSource.userPreferences.first()
            repository.refreshTopNews(
                country = prefs.defaultCountry,
                language = prefs.defaultLanguage,
                forceRefresh = forceRefresh,
            )
        }
    }

    suspend fun getLastCachedAt(): Long? = repository.getLastCachedAt()

    suspend fun getCountry(): String {
        return preferencesDataSource.userPreferences.first().defaultCountry
    }
}
