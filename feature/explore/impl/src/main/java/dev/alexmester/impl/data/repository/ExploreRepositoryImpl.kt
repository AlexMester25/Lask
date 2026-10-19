package dev.alexmester.impl.data.repository

import dev.alexmester.domain.cache.FeedCache
import dev.alexmester.domain.cache.FeedType
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.domain.source.NewsRemoteDataSource
import dev.alexmester.impl.domain.model.ExploreQuery
import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.news.SearchParams
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.result.runAppResult
import dev.alexmester.platform.dispatchers.DispatcherProvider
import dev.alexmester.utils.constants.LaskConstants
import dev.alexmester.utils.constants.LaskConstants.CACHE_TTL_MS
import dev.alexmester.utils.constants.LaskConstants.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExploreRepositoryImpl(
    private val remote: NewsRemoteDataSource,
    private val feedCache: FeedCache,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatchers: DispatcherProvider
) : ExploreRepository {

    override fun observeArticles(): Flow<List<NewsArticle>> =
        feedCache.observeClusters(FeedType.EXPLORE).map { clusters ->
            clusters.flatMap { it.articles }
        }

    override suspend fun getExploreQuery(): ExploreQuery {
        val prefs = userPreferencesRepository.observeUserPreferences().first()
        return ExploreQuery(
            interestsQuery = prefs.interests.joinToString(separator = LaskConstants.SEPARATOR_OR),
            language = prefs.defaultLanguage,
        )
    }

    override suspend fun refresh(
        force: Boolean,
        query: String,
        language: String
    ): AppResult<RefreshResult> =
        withContext(dispatchers.io) {
            if (!force && feedCache.isFresh(FeedType.EXPLORE, CACHE_TTL_MS)) {
                return@withContext AppResult.Success(RefreshResult.CacheFresh)
            }
            when (val result = fetchPage(query, language, offset = 0)) {
                is AppResult.Failure -> result
                is AppResult.Success -> {
                    if (result.data.isEmpty()) {
                        return@withContext AppResult.Success(RefreshResult.EmptySearchResult)
                    }
                    runAppResult<RefreshResult> {
                        feedCache.replace(
                            FeedType.EXPLORE,
                            listOf(NewsCluster(id = 0, articles = result.data)),
                        )
                        RefreshResult.Updated(result.data.size)
                    }
                }
            }
        }

    override suspend fun loadMore(
        query: String,
        language: String,
        offset: Int
    ): AppResult<RefreshResult> =
        withContext(dispatchers.io) {
            when (val result = fetchPage(query, language, offset)) {
                is AppResult.Failure -> result
                is AppResult.Success -> {
                    if (result.data.isEmpty()) {
                        return@withContext AppResult.Success(RefreshResult.EmptySearchResult)
                    }
                    runAppResult<RefreshResult> {
                        feedCache.append(FeedType.EXPLORE, result.data, positionStart = offset)
                        RefreshResult.Updated(result.data.size)
                    }
                }
            }
        }

    override suspend fun getLastCachedAt(): Long? = feedCache.lastCachedAt(FeedType.EXPLORE)

    private suspend fun fetchPage(query: String, language: String, offset: Int) =
        remote.search(
            SearchParams(
                text = query,
                textMatchIndexes = listOf("title"),
                language = language,
                offset = offset,
                number = PAGE_SIZE,
            )
        )
}