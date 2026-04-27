package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.BookmarksRepository

class RemoveBookmarksUseCase(
    private val repository: BookmarksRepository,
) {
    operator suspend fun invoke(ids: Set<Long>) =
        repository.removeBookmarks(ids)
}