package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ArticleDetailRepository

class GetArticleUseCase(
    private val repository: ArticleDetailRepository
) {
    operator suspend fun invoke(id: Long) = repository.getArticleById(id)
}