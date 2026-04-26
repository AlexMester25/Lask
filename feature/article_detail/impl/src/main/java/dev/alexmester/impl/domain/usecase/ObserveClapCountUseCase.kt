package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ArticleDetailRepository

class ObserveClapCountUseCase(
    private val repository: ArticleDetailRepository,
) {
    operator fun invoke(id: Long) = repository.observeClapCount(id)
}