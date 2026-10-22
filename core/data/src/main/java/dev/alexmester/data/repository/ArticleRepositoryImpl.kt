package dev.alexmester.data.repository

import dev.alexmester.data.mapper.toDomain
import dev.alexmester.data.mapper.toEntity
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.TransactionRunner
import dev.alexmester.database.entity.ArticleUserStateEntity
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.platform.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
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
        userStateDao.observeReadArticleIds().map { it.toSet() }

    override fun observeReadArticles(): Flow<List<NewsArticle>> =
        userStateDao.observeReadArticles().map { entities -> entities.map { it.toDomain() } }

    override fun observeClappedArticles(): Flow<List<NewsArticle>> =
        userStateDao.observeClappedArticles().map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveArticles(articles: List<NewsArticle>) =
        withContext(dispatchers.io) {
            articleDao.insertArticles(articles.map { it.toEntity() })
        }

    override suspend fun getArticleById(id: Long): NewsArticle? =
        withContext(dispatchers.io) { articleDao.getArticleById(id)?.toDomain() }

    override fun observeBookmarkedArticles(): Flow<List<NewsArticle>> =
        userStateDao.observeBookmarkedArticles().map { rows -> rows.map { it.toDomain() } }

    override fun observeIsBookmarked(articleId: Long): Flow<Boolean> =
        userStateDao.observeIsBookmarked(articleId).map { it ?: false }

    override suspend fun toggleBookmark(articleId: Long): Boolean =
        withContext(dispatchers.io) {
            var isBookmarked = false
            transactionRunner {
                ensureUserState(articleId)
                userStateDao.toggleBookmark(articleId, clock.millis())
                isBookmarked = userStateDao.isBookmarked(articleId) ?: false
            }
            isBookmarked
        }

    override suspend fun removeBookmarks(ids: Set<Long>) {
        if (ids.isEmpty()) return
        withContext(dispatchers.io) { userStateDao.clearBookmarks(ids) }
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
}