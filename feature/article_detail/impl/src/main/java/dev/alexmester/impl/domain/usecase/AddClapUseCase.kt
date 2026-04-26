package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ArticleDetailRepository

class AddClapUseCase(
    private val repository: ArticleDetailRepository,
) {
    operator suspend fun invoke(id: Long) = repository.addClap(id)
}