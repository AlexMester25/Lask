package dev.alexmester.domain.usecase

import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

class ObserveUserPreferencesUseCase(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<UserPreferences> =
        repository.observeUserPreferences()
}