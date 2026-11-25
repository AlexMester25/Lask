package dev.alexmester.testing.fake

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
 * Повторяет наблюдаемое поведение настоящего репозитория: закладки отдаются
 * окном [observeBookmarkedArticles], а их количество считается по всему набору,
 * поэтому `size == limit` не путается с «загружено всё».
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

    override fun observeReadArticles(): Flow<List<NewsArticle>> = readArticles.asStateFlow()

    override fun observeClappedArticles(): Flow<List<NewsArticle>> = clappedArticles.asStateFlow()

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
}
