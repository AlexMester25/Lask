package dev.alexmester.impl

import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Фейк остаётся в модуле фичи: [NewsFeedRepository] — внутренний контракт
 * `news_feed`, а `core` не может зависеть от фич.
 */
class FakeNewsFeedRepository : NewsFeedRepository {

    private val _clusters = MutableStateFlow<List<NewsCluster>>(emptyList())

    var refreshResult: AppResult<RefreshFeedResult> =
        AppResult.Success(RefreshFeedResult.Updated(10))

    /** Пока gate не завершён, [refreshFeed] остаётся приостановленным. */
    var refreshGate: CompletableDeferred<Unit>? = null

    var lastCachedAt: Long? = null

    private val _refreshForces = mutableListOf<Boolean>()

    /** Флаги `force` всех вызовов [refreshFeed] в порядке вызовов. */
    val refreshForces: List<Boolean>
        get() = _refreshForces

    val refreshCallCount: Int
        get() = _refreshForces.size

    var cancelledRefreshes: Int = 0
        private set

    override fun observeFeedClusters(): Flow<List<NewsCluster>> =
        _clusters.asStateFlow()

    override suspend fun refreshFeed(
        force: Boolean
    ): AppResult<RefreshFeedResult> {
        _refreshForces += force

        refreshGate?.let { gate ->
            try {
                gate.await()
            } catch (cancellation: CancellationException) {
                cancelledRefreshes++
                throw cancellation
            }
        }

        return refreshResult
    }

    override suspend fun getLastCachedAt(): Long? =
        lastCachedAt

    fun emitClusters(clusters: List<NewsCluster>) {
        _clusters.value = clusters
    }
}
