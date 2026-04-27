package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.NewsFeedRepository

class GetCurrentLocaleTrendsUseCase(
    private val repository: NewsFeedRepository,
) {
    suspend operator fun invoke(): Pair<String, String> =
        repository.getCurrentLocale()
}