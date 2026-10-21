package dev.alexmester.impl.data.repository

import dev.alexmester.domain.cache.FeedCache
import dev.alexmester.domain.cache.FeedType
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.domain.source.NewsRemoteDataSource
import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.result.runAppResult
import dev.alexmester.platform.dispatchers.DispatcherProvider
import dev.alexmester.utils.constants.LaskConstants.CACHE_TTL_MS
import dev.alexmester.domain.locale.checkCompatibility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NewsFeedRepositoryImpl(
    private val remote: NewsRemoteDataSource,
    private val feedCache: FeedCache,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatchers: DispatcherProvider,
) : NewsFeedRepository {

    override fun observeFeedClusters(): Flow<List<NewsCluster>> =
        feedCache.observeClusters(FeedType.TRENDS)

    override suspend fun refreshFeed(force: Boolean): AppResult<RefreshFeedResult> =
        withContext(dispatchers.io) {
            val prefs = userPreferencesRepository.observeUserPreferences().first()
            if (checkCompatibility(language = prefs.defaultLanguage, country = prefs.defaultCountry) != null) {
                return@withContext AppResult.Success(RefreshFeedResult.IncompatibleLocale)
            }
            if (!force && feedCache.isFresh(FeedType.TRENDS, CACHE_TTL_MS)) {
                return@withContext AppResult.Success(RefreshFeedResult.CacheFresh)
            }

            when (val result = remote.getTopNews(prefs.defaultCountry, prefs.defaultLanguage)) {
                is AppResult.Failure -> result
                is AppResult.Success -> runAppResult<RefreshFeedResult> {
                    feedCache.replace(FeedType.TRENDS, result.data)
                    RefreshFeedResult.Updated(result.data.size)
                }
            }
        }

    override suspend fun getLastCachedAt(): Long? = feedCache.lastCachedAt(FeedType.TRENDS)
}