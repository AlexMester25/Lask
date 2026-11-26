package dev.alexmester.testing.fake

import dev.alexmester.domain.articles.CategoryCount
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory реализация [ArticleRepository] для тестов.
 *
 * Повторяет наблюдаемое поведение настоящего репозитория:
 * - закладки, «Прочитанное» и «Клапы» отдаются окном по `limit`, а не целиком;
 * - фильтрация по категории и фасеты считаются по всему набору, а не по окну,
 *   поэтому `size == limit` не путается с «загружено всё»;
 * - статьи без категории образуют отдельную группу (`CategoryFilter.Uncategorized`).
 */
class FakeArticleRepository(
    articles: List<NewsArticle> = emptyList(),
) : ArticleRepository {

    private val storedArticles = articles.associateBy { it.id }.toMutableMap()

    private val readArticleIds = MutableStateFlow<Set<Long>>(emptySet())
    private val readArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    private val clappedArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    private val bookmarks = MutableStateFlow<List<NewsArticle>>(emptyList())
    private val clapCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())

    /** Лимиты, с которыми создавались потоки закладок, в порядке создания. */
    val requestedLimits = mutableListOf<Int>()

    /** Лимиты, с которыми создавались потоки «Прочитанного», в порядке создания. */
    val readRequestedLimits = mutableListOf<Int>()

    /** Лимиты, с которыми создавались потоки «Клапов», в порядке создания. */
    val clappedRequestedLimits = mutableListOf<Int>()

    /** Результат [saveArticles]; по умолчанию — успех. */
    var saveResult: AppResult<Unit> = AppResult.Success(Unit)

    fun setBookmarks(articles: List<NewsArticle>) {
        articles.forEach { storedArticles[it.id] = it }
        bookmarks.value = articles
    }

    fun emitReadArticleIds(ids: Set<Long>) {
        readArticleIds.value = ids
    }

    fun emitReadArticles(articles: List<NewsArticle>) {
        articles.forEach { storedArticles[it.id] = it }
        readArticles.value = articles
    }

    fun emitClappedArticles(articles: List<NewsArticle>) {
        articles.forEach { storedArticles[it.id] = it }
        clappedArticles.value = articles
    }

    override fun observeReadArticleIds(): Flow<Set<Long>> = readArticleIds.asStateFlow()

    override fun observeReadArticles(
        filter: CategoryFilter,
        limit: Int,
    ): Flow<List<NewsArticle>> {
        readRequestedLimits += limit
        return readArticles.map { it.applyFilter(filter).take(limit) }
    }

    override fun observeReadCategoryCounts(): Flow<List<CategoryCount>> =
        readArticles.map { it.toCategoryCounts() }

    override fun observeClappedArticles(
        filter: CategoryFilter,
        limit: Int,
    ): Flow<List<NewsArticle>> {
        clappedRequestedLimits += limit
        return clappedArticles.map { it.applyFilter(filter).take(limit) }
    }

    override fun observeClappedCategoryCounts(): Flow<List<CategoryCount>> =
        clappedArticles.map { it.toCategoryCounts() }

    override suspend fun saveArticles(articles: List<NewsArticle>): AppResult<Unit> {
        articles.forEach { storedArticles[it.id] = it }
        return saveResult
    }

    override suspend fun getArticleById(id: Long): NewsArticle? = storedArticles[id]

    override fun observeBookmarkedArticles(limit: Int): Flow<List<NewsArticle>> {
        requestedLimits += limit
        return bookmarks.map { it.take(limit) }
    }

    override fun observeBookmarkedCount(): Flow<Int> = bookmarks.map { it.size }

    override fun observeIsBookmarked(articleId: Long): Flow<Boolean> =
        bookmarks.map { articles -> articles.any { it.id == articleId } }

    override suspend fun toggleBookmark(articleId: Long): Boolean {
        val article = storedArticles[articleId] ?: return false
        val wasBookmarked = bookmarks.value.any { it.id == articleId }
        bookmarks.value = if (wasBookmarked) {
            bookmarks.value.filterNot { it.id == articleId }
        } else {
            listOf(article) + bookmarks.value
        }
        return !wasBookmarked
    }

    override suspend fun removeBookmarks(ids: Set<Long>) {
        bookmarks.update { articles -> articles.filterNot { it.id in ids } }
    }

    override fun observeClapCount(articleId: Long): Flow<Int> =
        clapCounts.map { counts -> counts[articleId] ?: 0 }

    override suspend fun addClap(articleId: Long) {
        clapCounts.update { counts ->
            counts + (articleId to (counts[articleId] ?: 0) + 1)
        }
    }

    override suspend fun markAsRead(articleId: Long): Boolean {
        val isNew = articleId !in readArticleIds.value
        if (isNew) readArticleIds.update { ids -> ids + articleId }
        return isNew
    }

    override fun observeReadCount(): Flow<Int> = readArticleIds.map { it.size }

    private fun List<NewsArticle>.applyFilter(filter: CategoryFilter): List<NewsArticle> =
        when (filter) {
            CategoryFilter.All -> this
            CategoryFilter.Uncategorized -> filter { it.category == null }
            is CategoryFilter.Named -> filter { it.category == filter.name }
        }

    /**
     * Повторяет `GROUP BY category ... ORDER BY COUNT(*) DESC, category ASC`:
     * null-категория при равенстве размеров идёт первой, как NULL в SQLite ASC.
     */
    private fun List<NewsArticle>.toCategoryCounts(): List<CategoryCount> =
        groupBy { it.category }
            .map { (category, articles) -> CategoryCount(category = category, count = articles.size) }
            .sortedWith(
                compareByDescending<CategoryCount> { it.count }
                    .thenBy { it.category ?: "" },
            )
}
