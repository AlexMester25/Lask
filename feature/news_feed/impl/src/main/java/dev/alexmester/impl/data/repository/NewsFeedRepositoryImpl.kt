package dev.alexmester.impl.data.repository

import dev.alexmester.database.entity.FeedCacheEntity.Companion.TRENDS_FEED
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.mapper.toEntities
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.extension.safeApiCall
import dev.alexmester.platform.dispatchers.DispatcherProvider
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

    override fun observeFeedClusters(): Flow<List<NewsCluster>> =
        local.observeFeedClusters()

    override fun observeReadArticleIds(): Flow<List<Long>> =
        local.observeReadArticleIds()

    override fun observeUserPreferences(): Flow<UserPreferences> =
        preferencesDataSource.userPreferences

    override suspend fun refreshFeed(): AppResult<Int> =
        withContext(dispatchers.io) {
            safeApiCall {
                val prefs = preferencesDataSource.userPreferences.first()
                if (checkCompatibility(
                        language = prefs.defaultLanguage,
                        country = prefs.defaultCountry,
                    ) != null
                ) {
                    return@safeApiCall 0
                }

                val response = remote.getTopNews(
                    sourceCountry = prefs.defaultCountry,
                    language = prefs.defaultLanguage
                )

                val (articles, feedCache) = withContext(dispatchers.default) {
                    response.topNews.toEntities(TRENDS_FEED)
                }

                local.replaceFeedCache(articles = articles, feedCache = feedCache)
                response.topNews.size
            }
        }


    override suspend fun getLastCachedAt(): Long? =
        withContext(dispatchers.io) {
            local.getLastCachedAt()
        }
}