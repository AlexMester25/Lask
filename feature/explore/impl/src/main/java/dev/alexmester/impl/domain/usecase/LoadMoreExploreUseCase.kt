package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.result.AppResult

class LoadMoreExploreUseCase(
    private val repository: ExploreRepository,
) {

    suspend operator fun invoke(offset: Int): AppResult<RefreshResult> {
        val (query, language) = repository.getExploreQuery()
        if (query.isEmpty()) return AppResult.Success(RefreshResult.NoInterests)

        return repository.loadMore(query = query, language = language, offset = offset)
    }
}