package dev.alexmester.impl.data.repository

import dev.alexmester.database.entity.FeedCacheEntity.Companion.TRENDS_FEED
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.mapper.toEntities
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.error.WorldNewsErrorMapper
import dev.alexmester.network.extension.safeApiCall
import dev.alexmester.platform.dispatchers.DispatcherProvider
import dev.alexmester.utils.constants.LaskConstants.CACHE_TTL_MS
import dev.alexmester.utils.locale.checkCompatibility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NewsFeedRepositoryImpl(
    private val remote: NewsFeedApiService,
    private val local: NewsFeedLocalDataSource,
    private val preferencesDataSource: UserPreferencesDataSource,
    private val dispatchers: DispatcherProvider,
) : NewsFeedRepository {

    private val errorMapper = WorldNewsErrorMapper()

    override fun observeFeedClusters(): Flow<List<NewsCluster>> =
        local.observeFeedClusters()

    override fun observeReadArticleIds(): Flow<List<Long>> =
        local.observeReadArticleIds()

    override fun observeUserPreferences(): Flow<UserPreferences> =
        preferencesDataSource.userPreferences

    override suspend fun refreshFeed(force: Boolean): AppResult<RefreshFeedResult> =
        withContext(dispatchers.io) {
            val prefs = preferencesDataSource.userPreferences.first()

            val lastCachedAt = local.getLastCachedAt()

            val isFresh = lastCachedAt != null &&
                    (System.currentTimeMillis() - lastCachedAt) < CACHE_TTL_MS

            if (!force && isFresh) {
                return@withContext AppResult.Success(
                    RefreshFeedResult.CacheFresh
                )
            }

            safeApiCall(errorMapper) {

                if (checkCompatibility(
                        language = prefs.defaultLanguage,
                        country = prefs.defaultCountry,
                    ) != null
                ) {
                    return@safeApiCall RefreshFeedResult.IncompatibleLocale
                }

                val response = remote.getTopNews(
                    sourceCountry = prefs.defaultCountry,
                    language = prefs.defaultLanguage
                )

                val (articles, feedCache) = withContext(dispatchers.default) {
                    response.topNews.toEntities(TRENDS_FEED)
                }

                local.replaceFeedCache(articles = articles, feedCache = feedCache)

                RefreshFeedResult.Updated(response.topNews.size)
            }
        }


    override suspend fun getLastCachedAt(): Long? =
        withContext(dispatchers.io) {
            local.getLastCachedAt()
        }
}