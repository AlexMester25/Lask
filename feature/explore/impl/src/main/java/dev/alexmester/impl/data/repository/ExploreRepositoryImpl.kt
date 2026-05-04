package dev.alexmester.impl.data.repository

import dev.alexmester.database.entity.FeedCacheEntity.Companion.EXPLORE_FEED
import dev.alexmester.impl.data.local.ExploreLocalDataSource
import dev.alexmester.impl.data.mapper.toEntities
import dev.alexmester.impl.data.remote.ExploreApiService
import dev.alexmester.impl.domain.model.ExploreQuery
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.extension.safeApiCall
import dev.alexmester.platform.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExploreRepositoryImpl(
    private val remote: ExploreApiService,
    private val local: ExploreLocalDataSource,
    private val dispatchers: DispatcherProvider
) : ExploreRepository {

    override fun observeArticles(): Flow<List<NewsArticle>> =
        local.observeFeedArticles()

    override fun observeReadArticleIds(): Flow<List<Long>> =
        local.observeReadArticleIds()

    override suspend fun getExploreQuery(): ExploreQuery =
        local.getExploreQuery()

    override suspend fun refresh(
        query: String,
        language: String,
    ): AppResult<Int> = withContext(dispatchers.io) {
        safeApiCall {
            val response = remote.searchNews(
                text = query,
                language = language,
                offset = 0,
            )
            if (response.news.isEmpty()) return@safeApiCall 0
            val (articles, cache) = withContext(dispatchers.default) {
                response.news.toEntities(feedType = EXPLORE_FEED, positionStart = 0)
            }
            local.refreshFeed(articles = articles, feedCache = cache)
            response.news.size
        }
    }


    override suspend fun loadMore(
        query: String,
        language: String,
        offset: Int,
    ): AppResult<Int> = withContext(dispatchers.io) {
        safeApiCall {
            val response = remote.searchNews(
                text = query,
                language = language,
                offset = offset,
            )
            if (response.news.isEmpty()) return@safeApiCall 0
            val (articles, cache) = withContext(dispatchers.default) {
                response.news.toEntities(feedType = EXPLORE_FEED, positionStart = offset)
            }
            local.loadMoreFeed(articles = articles, feedCache = cache)
            response.news.size
        }
    }


    override suspend fun getLastCachedAt(): Long? =
        withContext(dispatchers.io) {
            local.getLastCachedAt()
        }
}