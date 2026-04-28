package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ProfileRepository

class RemoveInterestUseCase(
    private val repository: ProfileRepository,
) {
    operator suspend fun invoke(keyWord: String) =
        repository.removeInterest(keyWord)
}