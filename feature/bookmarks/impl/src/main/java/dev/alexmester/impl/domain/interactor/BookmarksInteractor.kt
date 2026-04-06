package dev.alexmester.impl.domain.interactor

import dev.alexmester.impl.domain.repository.BookmarksRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class BookmarksInteractor(
    private val repository: BookmarksRepository,
) {
    fun getBookmarksFlow(): Flow<List<NewsArticle>> = repository.getAllBookmarks()

    suspend fun deleteBookmarks(ids: Set<Long>) = repository.deleteBookmarks(ids)
}