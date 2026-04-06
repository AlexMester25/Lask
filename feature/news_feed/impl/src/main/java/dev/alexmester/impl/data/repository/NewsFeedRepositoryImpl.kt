package dev.alexmester.impl.data.repository

import dev.alexmester.database.entity.NewsArticleEntity.Companion.SOURCE_FEED
import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.mapper.dtosToEntities
import dev.alexmester.impl.data.mapper.entitiesToClusters
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.ext.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NewsFeedRepositoryImpl(
    private val remote: NewsFeedApiService,
    private val local: NewsFeedLocalDataSource,
) : NewsFeedRepository {

    override fun getClustersFlow(): Flow<List<NewsCluster>> =
        local.getArticles().map {
            withContext(Dispatchers.Default) {
                it.entitiesToClusters()
            }
        }

    override fun getReadArticleIdsFlow(): Flow<List<Long>> =
        local.getReadArticleIds()

    override suspend fun refreshTopNews(
        country: String,
        language: String,
    ): AppResult<Unit> = safeApiCall {
        val response = remote.getTopNews(sourceCountry = country, language = language)
        val entities = withContext(Dispatchers.Default) {
            response.topNews.dtosToEntities(SOURCE_FEED)
        }
        local.replaceArticles(entities)
    }

    override suspend fun getLastCachedAt(): Long? =
        local.getLastCachedAt()
}