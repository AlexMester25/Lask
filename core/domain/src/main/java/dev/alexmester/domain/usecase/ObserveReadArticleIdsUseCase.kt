package dev.alexmester.domain.usecase

import dev.alexmester.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class ObserveReadArticleIdsUseCase(
    private val repository: ArticleRepository
) {
    operator fun invoke(): Flow<List<Long>> =
        repository.observeReadArticleIds()
}