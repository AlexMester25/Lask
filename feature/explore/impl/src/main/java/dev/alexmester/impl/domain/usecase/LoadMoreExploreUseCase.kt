package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.RefreshResult
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.result.map
import dev.alexmester.utils.constants.LaskConstants.PAGE_SIZE
import dev.alexmester.utils.extension.mutex.withTryLock
import kotlinx.coroutines.sync.Mutex

class LoadMoreExploreUseCase(
    private val repository: ExploreRepository,
    private val getQuery: GetInterestsExploreUseCase,
) {

    private val mutex = Mutex()

    suspend operator fun invoke(offset: Int): AppResult<RefreshResult> =
        mutex.withTryLock {
            val (query, language) = getQuery()
            if (query.isEmpty()) return@withTryLock AppResult.Success(RefreshResult.NoInterests)

            repository.loadMore(query = query, language = language, offset = offset)
                .map { count ->
                    if (count == 0) RefreshResult.EmptySearchResult
                    else RefreshResult.Success(count)
                }
        } ?: AppResult.Success(RefreshResult.NoInterests)
}