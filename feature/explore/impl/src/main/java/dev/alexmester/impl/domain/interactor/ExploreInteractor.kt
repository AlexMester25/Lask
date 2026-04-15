package dev.alexmester.impl.domain.interactor

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ExploreInteractor(
    private val repository: ExploreRepository,
    private val preferencesDataSource: UserPreferencesDataSource,
) {

    private val refreshMutex = Mutex()
    private val loadMoreMutex = Mutex()

    fun observeArticles(): Flow<List<NewsArticle>> = repository.observeArticles()

    fun observeReadArticleIds(): Flow<List<Long>> = repository.observeReadArticleIds()

    suspend fun getExploreQueryOrNull(): String? {
        val interests = preferencesDataSource.userPreferences.first().interests
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (interests.isEmpty()) return null
        return interests.joinToString(" OR ")
    }

    suspend fun getLanguage(): String = preferencesDataSource.userPreferences.first().defaultLanguage

    suspend fun refresh(pageSize: Int): AppResult<Int> {
        if (refreshMutex.isLocked) return AppResult.Success(0)
        return refreshMutex.withLock {
            val query = getExploreQueryOrNull() ?: return@withLock AppResult.Success(0)
            val language = getLanguage()
            repository.refresh(query = query, language = language, pageSize = pageSize)
        }
    }

    suspend fun loadMore(pageSize: Int, offset: Int): AppResult<Int> {
        if (loadMoreMutex.isLocked) return AppResult.Success(0)
        return loadMoreMutex.withLock {
            val query = getExploreQueryOrNull() ?: return@withLock AppResult.Success(0)
            val language = getLanguage()
            repository.loadMore(
                query = query,
                language = language,
                pageSize = pageSize,
                offset = offset,
            )
        }
    }

    suspend fun getLastCachedAt(): Long? = repository.getLastCachedAt()
}