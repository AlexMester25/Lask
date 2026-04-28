package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ProfileRepository

class AddInterestUseCase(
    private val repository: ProfileRepository,
) {
    operator suspend fun invoke(keyWord: String) =
        repository.addInterest(keyWord)
}