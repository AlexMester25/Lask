package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ArticleDetailRepository

class MarkAsReadUseCase(
    private val repository: ArticleDetailRepository,
) {
    operator suspend fun invoke(id: Long) = repository.markAsRead(id)
}