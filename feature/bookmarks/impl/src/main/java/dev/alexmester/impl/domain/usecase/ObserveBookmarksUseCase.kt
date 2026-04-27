package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.BookmarksRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ObserveBookmarksUseCase(
    private val repository: BookmarksRepository,
) {
    operator fun invoke(): Flow<List<NewsArticle>> =
        repository.observeBookmarks()
}