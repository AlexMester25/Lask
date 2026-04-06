package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {
    fun getAllBookmarks(): Flow<List<NewsArticle>>
    suspend fun deleteBookmarks(ids: Set<Long>)
}