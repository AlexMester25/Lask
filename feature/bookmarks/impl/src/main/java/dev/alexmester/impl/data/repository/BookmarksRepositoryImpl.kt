package dev.alexmester.impl.data.repository

import dev.alexmester.impl.data.local.BookmarksLocalDataSource
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.impl.domain.repository.BookmarksRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.platform.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookmarksRepositoryImpl(
    private val local: BookmarksLocalDataSource,
    private val dispatchers: DispatcherProvider,
) : BookmarksRepository {

    override fun observeBookmarks(): Flow<List<NewsArticle>> =
        local.observeBookmarkedArticles().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun removeBookmarks(ids: Set<Long>) =
        withContext(dispatchers.io) {
            local.removeBookmarks(ids)
        }
}
