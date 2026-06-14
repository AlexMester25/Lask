package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.InterestsRepository

class RemoveInterestUseCase(
    private val repository: InterestsRepository,
) {
    operator suspend fun invoke(keyWord: String) =
        repository.removeInterest(keyWord)
}