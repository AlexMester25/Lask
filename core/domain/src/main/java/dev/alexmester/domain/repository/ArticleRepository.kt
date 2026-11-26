package dev.alexmester.domain.repository

import dev.alexmester.domain.articles.CategoryCount
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun observeReadArticleIds(): Flow<Set<Long>>

    /**
     * Окно «Прочитанного»: не более [limit] самых свежих статей, отфильтрованных
     * по [filter]. Фильтрация выполняется в SQL, поэтому окно не искажает категории.
     */
    fun observeReadArticles(filter: CategoryFilter, limit: Int): Flow<List<NewsArticle>>

    /** Размер каждой категории «Прочитанного» по всему списку — для чипов и `canLoadMore`. */
    fun observeReadCategoryCounts(): Flow<List<CategoryCount>>

    /** Окно «Клапов»: не более [limit] статей с наибольшим числом клапов по [filter]. */
    fun observeClappedArticles(filter: CategoryFilter, limit: Int): Flow<List<NewsArticle>>

    /** Размер каждой категории «Клапов» по всему списку. */
    fun observeClappedCategoryCounts(): Flow<List<CategoryCount>>

    /** Сохраняет статьи локально. Сбой хранилища возвращается как [AppResult.Failure]. */
    suspend fun saveArticles(articles: List<NewsArticle>): AppResult<Unit>

    suspend fun getArticleById(id: Long): NewsArticle?

    /** Закладки, начиная с самых свежих, не более [limit] штук. */
    fun observeBookmarkedArticles(limit: Int): Flow<List<NewsArticle>>

    /** Общее количество закладок — нужно, чтобы понять, есть ли ещё страницы. */
    fun observeBookmarkedCount(): Flow<Int>
    fun observeIsBookmarked(articleId: Long): Flow<Boolean>
    suspend fun toggleBookmark(articleId: Long): Boolean
    suspend fun removeBookmarks(ids: Set<Long>)

    fun observeClapCount(articleId: Long): Flow<Int>
    suspend fun addClap(articleId: Long)

    suspend fun markAsRead(articleId: Long): Boolean
    fun observeReadCount(): Flow<Int>
}
