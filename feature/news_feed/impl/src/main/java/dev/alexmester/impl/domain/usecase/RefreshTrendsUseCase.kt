package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.models.result.AppResult

class RefreshTrendsUseCase(
    private val repository: NewsFeedRepository,
) {
    suspend operator fun invoke(force: Boolean): AppResult<RefreshFeedResult> =
        repository.refreshFeed(force)
}