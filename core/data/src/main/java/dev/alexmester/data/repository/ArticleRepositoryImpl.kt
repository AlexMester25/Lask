package dev.alexmester.data.repository

import dev.alexmester.data.extension.runStorageResult
import dev.alexmester.data.mapper.toDomain
import dev.alexmester.data.mapper.toEntity
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.TransactionRunner
import dev.alexmester.database.entity.ArticleUserStateEntity
import dev.alexmester.domain.articles.CategoryCount
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.platform.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Clock

internal class ArticleRepositoryImpl (
    private val userStateDao: ArticleUserStateDao,
    private val articleDao: ArticleDao,
    private val transactionRunner: TransactionRunner,
    private val dispatchers: DispatcherProvider,
    private val clock: Clock,
): ArticleRepository {

    override fun observeReadArticleIds(): Flow<Set<Long>> =
        userStateDao.observeReadArticleIds()
            .map { it.toSet() }
            .flowOn(dispatchers.io)

    override fun observeReadArticles(
        filter: CategoryFilter,
        limit: Int,
    ): Flow<List<NewsArticle>> {
        val (category, onlyUncategorized) = filter.toQuery()
        return userStateDao.observeReadArticles(category, onlyUncategorized, limit)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.io)
    }

    override fun observeReadCategoryCounts(): Flow<List<CategoryCount>> =
        userStateDao.observeReadCategoryCounts()
            .map { rows -> rows.map { CategoryCount(category = it.category, count = it.count) } }
            .flowOn(dispatchers.io)

    override fun observeClappedArticles(
        filter: CategoryFilter,
        limit: Int,
    ): Flow<List<NewsArticle>> {
        val (category, onlyUncategorized) = filter.toQuery()
        return userStateDao.observeClappedArticles(category, onlyUncategorized, limit)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.io)
    }

    override fun observeClappedCategoryCounts(): Flow<List<CategoryCount>> =
        userStateDao.observeClappedCategoryCounts()
            .map { rows -> rows.map { CategoryCount(category = it.category, count = it.count) } }
            .flowOn(dispatchers.io)

    override suspend fun saveArticles(articles: List<NewsArticle>): AppResult<Unit> =
        runStorageResult {
            withContext(dispatchers.io) {
                articleDao.insertArticles(articles.map { it.toEntity() })
            }
        }

    override suspend fun getArticleById(id: Long): NewsArticle? =
        withContext(dispatchers.io) { articleDao.getArticleById(id)?.toDomain() }

    override fun observeBookmarkedArticles(limit: Int): Flow<List<NewsArticle>> =
        userStateDao.observeBookmarkedArticles(limit)
            .map { rows -> rows.map { it.toDomain() } }
            .flowOn(dispatchers.io)

    override fun observeBookmarkedCount(): Flow<Int> = userStateDao.observeBookmarkedCount()

    override fun observeIsBookmarked(articleId: Long): Flow<Boolean> =
        userStateDao.observeIsBookmarked(articleId).map { it ?: false }

    override suspend fun toggleBookmark(articleId: Long): Boolean =
        withContext(dispatchers.io) {
            var isBookmarked = false
            transactionRunner {
                ensureUserState(articleId)
                userStateDao.toggleBookmark(articleId, clock.millis())
                isBookmarked = userStateDao.isBookmarked(articleId) ?: false
                if (!isBookmarked) userStateDao.deleteEmptyStates(setOf(articleId))
            }
            isBookmarked
        }

    override suspend fun removeBookmarks(ids: Set<Long>) {
        if (ids.isEmpty()) return
        withContext(dispatchers.io) {
            transactionRunner {
                userStateDao.clearBookmarks(ids)
                userStateDao.deleteEmptyStates(ids)
            }
        }
    }

    override fun observeClapCount(articleId: Long): Flow<Int> =
        userStateDao.observeClapCount(articleId).map { it ?: 0 }

    override suspend fun addClap(articleId: Long) = withContext(dispatchers.io) {
        ensureUserState(articleId)
        userStateDao.incrementClap(articleId)
    }

    override suspend fun markAsRead(articleId: Long): Boolean = withContext(dispatchers.io) {
        ensureUserState(articleId)
        userStateDao.markAsRead(articleId, clock.millis()) > 0
    }

    override fun observeReadCount(): Flow<Int> = userStateDao.observeReadCount()

    private suspend fun ensureUserState(articleId: Long) =
        userStateDao.insertIfAbsent(ArticleUserStateEntity(articleId = articleId))

    /**
     * Разворачивает доменный фильтр в параметры SQL: `(category, onlyUncategorized)`.
     * `All` и `Uncategorized` оба дают `category == null`, но различаются вторым флагом.
     */
    private fun CategoryFilter.toQuery(): Pair<String?, Boolean> = when (this) {
        CategoryFilter.All -> null to false
        CategoryFilter.Uncategorized -> null to true
        is CategoryFilter.Named -> name to false
    }
}