package dev.alexmester.impl

import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeNewsFeedRepository : NewsFeedRepository {

    private val _clusters = MutableStateFlow<List<NewsCluster>>(emptyList())
    private val _readIds = MutableStateFlow<List<Long>>(emptyList())
    private val _userPrefs = MutableStateFlow(
        UserPreferences(
            defaultCountry = "us",
            defaultLanguage = "en"
        )
    )

    var refreshResult: AppResult<RefreshFeedResult> =
        AppResult.Success(RefreshFeedResult.Updated(10))

    var lastCachedAt: Long? = null

    var refreshCallCount: Int = 0
        private set

    var lastForce: Boolean = false
        private set

    override fun observeFeedClusters(): Flow<List<NewsCluster>> =
        _clusters.asStateFlow()

    override suspend fun refreshFeed(
        force: Boolean
    ): AppResult<RefreshFeedResult> {
        refreshCallCount++
        lastForce = force

        return refreshResult
    }

    override suspend fun getLastCachedAt(): Long? =
        lastCachedAt

    fun emitUserPreferences(prefs: UserPreferences) {
        _userPrefs.value = prefs
    }

    fun emitClusters(clusters: List<NewsCluster>) {
        _clusters.value = clusters
    }

    fun emitReadIds(ids: List<Long>) {
        _readIds.value = ids
    }
}

// ── Test builders ─────────────────────────────────────────────────────────────

fun buildArticle(id: Long = 1L) = NewsArticle(
    id = id,
    title = "Title $id",
    text = null,
    summary = null,
    url = "https://example.com/$id",
    image = null,
    video = null,
    publishDate = "2026-04-26",
    authors = emptyList(),
    category = null,
    language = "en",
    sourceCountry = "us",
    sentiment = null,
)

fun buildCluster(id: Int, articleCount: Int = 2) = NewsCluster(
    id = id,
    articles = (1..articleCount).map { buildArticle(id.toLong() * 10 + it) },
)