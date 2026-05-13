package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.result.map
import dev.alexmester.utils.extension.mutex.withTryLock
import kotlinx.coroutines.sync.Mutex

class LoadMoreExploreUseCase(
    private val repository: ExploreRepository,
    private val getQuery: GetInterestsExploreUseCase,
) {

    suspend operator fun invoke(offset: Int): AppResult<RefreshResult> {
        val (query, language) = getQuery()
        if (query.isEmpty()) return AppResult.Success(RefreshResult.NoInterests)

        return repository.loadMore(query = query, language = language, offset = offset)
    }
}