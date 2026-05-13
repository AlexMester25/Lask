package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.result.AppResult

class RefreshExploreUseCase(
    private val repository: ExploreRepository,
    private val getQuery: GetInterestsExploreUseCase,
) {

    suspend operator fun invoke(force: Boolean): AppResult<RefreshResult>{
        val (query, language) = getQuery()
        if (query.isEmpty()) return AppResult.Success(RefreshResult.NoInterests)

        return repository.refresh(force = force, query = query, language = language)
    }
}