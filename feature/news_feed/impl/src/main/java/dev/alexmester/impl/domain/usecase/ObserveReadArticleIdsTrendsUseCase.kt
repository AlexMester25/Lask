package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow

class ObserveReadArticleIdsTrendsUseCase(
    private val repository: NewsFeedRepository,
) {
    operator fun invoke(): Flow<List<Long>> =
        repository.observeReadArticleIds()
}