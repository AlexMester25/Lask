package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.ExploreQuery
import dev.alexmester.impl.domain.repository.ExploreRepository

class GetInterestsExploreUseCase(
    private val repository: ExploreRepository,
) {
    suspend operator fun invoke(): ExploreQuery =
        repository.getExploreQuery()
}