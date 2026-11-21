package dev.alexmester.impl

import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

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

class FakeUserPreferencesRepository(
    initial: UserPreferences = UserPreferences(
        defaultCountry = "us",
        defaultLanguage = "en",
    ),
) : UserPreferencesRepository {

    private val _preferences = MutableStateFlow(initial)

    val preferences: UserPreferences
        get() = _preferences.value

    override fun observeUserPreferences(): Flow<UserPreferences> =
        _preferences.asStateFlow()

    fun emit(preferences: UserPreferences) {
        _preferences.value = preferences
    }

    override suspend fun updateTheme(isDark: Boolean?) = Unit
    override suspend fun updateLocaleManually(country: String, language: String) = Unit
    override suspend fun updateAutoTranslateLanguage(language: String) = Unit
    override suspend fun updateProfile(name: String, avatarUri: String?) = Unit
    override suspend fun updateStreak() = Unit
    override suspend fun addInterest(keyword: String) = Unit
    override suspend fun removeInterest(keyword: String) = Unit
    override suspend fun addXp(delta: Float) = Unit
    override suspend fun completeOnboarding() = Unit
    override suspend fun initLocaleFromDevice(country: String, language: String) = Unit
}

class FakeArticleRepository : ArticleRepository {

    private val _readArticleIds = MutableStateFlow<Set<Long>>(emptySet())

    override fun observeReadArticleIds(): Flow<Set<Long>> =
        _readArticleIds.asStateFlow()

    fun emitReadArticleIds(ids: Set<Long>) {
        _readArticleIds.value = ids
    }

    override fun observeReadArticles(): Flow<List<NewsArticle>> =
        flowOf(emptyList())

    override fun observeClappedArticles(): Flow<List<NewsArticle>> =
        flowOf(emptyList())

    override suspend fun saveArticles(articles: List<NewsArticle>): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun getArticleById(id: Long): NewsArticle? = null

    override fun observeBookmarkedArticles(): Flow<List<NewsArticle>> =
        flowOf(emptyList())

    override fun observeIsBookmarked(articleId: Long): Flow<Boolean> =
        flowOf(false)

    override suspend fun toggleBookmark(articleId: Long): Boolean = false

    override suspend fun removeBookmarks(ids: Set<Long>) = Unit

    override fun observeClapCount(articleId: Long): Flow<Int> =
        flowOf(0)

    override suspend fun addClap(articleId: Long) = Unit

    override suspend fun markAsRead(articleId: Long): Boolean = false

    override fun observeReadCount(): Flow<Int> =
        flowOf(0)
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
