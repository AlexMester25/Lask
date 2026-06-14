package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.InterestsRepository

class AddInterestUseCase(
    private val repository: InterestsRepository,
) {
    operator suspend fun invoke(keyWord: String) =
        repository.addInterest(keyWord)
}