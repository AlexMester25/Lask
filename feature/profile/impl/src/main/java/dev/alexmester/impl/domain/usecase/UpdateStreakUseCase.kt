package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ProfileRepository

class UpdateStreakUseCase(
    private val repository: ProfileRepository,
) {
    operator suspend fun invoke() = repository.updateStreak()
}