package dev.alexmester.impl.data.repository

import dev.alexmester.impl.data.local.BookmarksLocalDataSource
import dev.alexmester.impl.data.mapper.toDomain
import dev.alexmester.impl.domain.repository.BookmarksRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarksRepositoryImpl(
    private val local: BookmarksLocalDataSource,
) : BookmarksRepository {

    override fun getAllBookmarks(): Flow<List<NewsArticle>> =
        local.getAllBookmarks().map { entities -> entities.map { it.toDomain() } }

    override suspend fun deleteBookmarks(ids: Set<Long>) =
        local.deleteBookmarks(ids)
}