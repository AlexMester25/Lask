package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetReadArticleIdsSearchUseCase(
    private val repository: SearchRepository,
) {
    operator fun invoke(): Flow<List<Long>> =
        repository.getReadArticleIdsFlow()
}