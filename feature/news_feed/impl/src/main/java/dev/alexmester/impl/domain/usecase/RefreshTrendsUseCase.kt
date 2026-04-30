package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.result.AppResult

class RefreshTrendsUseCase(
    private val repository: NewsFeedRepository,
) {
    suspend operator fun invoke(): AppResult<Int> =
        repository.refreshFeed()
}