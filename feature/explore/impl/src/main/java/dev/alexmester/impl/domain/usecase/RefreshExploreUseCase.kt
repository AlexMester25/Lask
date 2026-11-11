package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.RefreshExploreResult
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.result.AppResult

class RefreshExploreUseCase(
    private val repository: ExploreRepository,
) {

    suspend operator fun invoke(force: Boolean): AppResult<RefreshExploreResult>{
        val (query, language) = repository.getExploreQuery()
        if (query.isEmpty()) return AppResult.Success(RefreshExploreResult.NoInterests)

        return repository.refresh(force = force, query = query, language = language)
    }
}