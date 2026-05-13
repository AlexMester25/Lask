package dev.alexmester.impl.data.repository

import dev.alexmester.database.entity.FeedCacheEntity.Companion.EXPLORE_FEED
import dev.alexmester.impl.data.local.ExploreLocalDataSource
import dev.alexmester.impl.data.mapper.toEntities
import dev.alexmester.impl.data.remote.ExploreApiService
import dev.alexmester.impl.domain.model.ExploreQuery
import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.error.WorldNewsErrorMapper
import dev.alexmester.network.extension.safeApiCall
import dev.alexmester.platform.dispatchers.DispatcherProvider
import dev.alexmester.utils.constants.LaskConstants.CACHE_TTL_MS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExploreRepositoryImpl(
    private val remote: ExploreApiService,
    private val local: ExploreLocalDataSource,
    private val dispatchers: DispatcherProvider
) : ExploreRepository {

    private val errorMapper = WorldNewsErrorMapper()

    override fun observeArticles(): Flow<List<NewsArticle>> =
        local.observeFeedArticles()

    override fun observeReadArticleIds(): Flow<List<Long>> =
        local.observeReadArticleIds()

    override suspend fun getExploreQuery(): ExploreQuery =
        local.getExploreQuery()

    override suspend fun refresh(
        force: Boolean,
        query: String,
        language: String,
    ): AppResult<RefreshResult> = withContext(dispatchers.io) {
        val lastCachedAt = local.getLastCachedAt()

        val isFresh = lastCachedAt != null &&
                (System.currentTimeMillis() - lastCachedAt) < CACHE_TTL_MS

        if (!force && isFresh) {
            return@withContext AppResult.Success(
                RefreshResult.CacheFresh
            )
        }

        safeApiCall(errorMapper) {
            val response = remote.searchNews(
                text = query,
                language = language,
                offset = 0,
            )

            if (response.news.isEmpty()) return@safeApiCall RefreshResult.EmptySearchResult

            val (articles, cache) = withContext(dispatchers.default) {
                response.news.toEntities(feedType = EXPLORE_FEED, positionStart = 0)
            }

            local.refreshFeed(articles = articles, feedCache = cache)

            RefreshResult.Updated(response.news.size)
        }
    }

    override suspend fun loadMore(
        query: String,
        language: String,
        offset: Int,
    ): AppResult<RefreshResult> = withContext(dispatchers.io) {
        safeApiCall(errorMapper) {
            val response = remote.searchNews(
                text = query,
                language = language,
                offset = offset,
            )

            if (response.news.isEmpty()) return@safeApiCall RefreshResult.EmptySearchResult

            val (articles, cache) = withContext(dispatchers.default) {
                response.news.toEntities(feedType = EXPLORE_FEED, positionStart = offset)
            }

            local.loadMoreFeed(articles = articles, feedCache = cache)

            RefreshResult.Updated(response.news.size)
        }
    }

    override suspend fun getLastCachedAt(): Long? =
        withContext(dispatchers.io) {
            local.getLastCachedAt()
        }
}