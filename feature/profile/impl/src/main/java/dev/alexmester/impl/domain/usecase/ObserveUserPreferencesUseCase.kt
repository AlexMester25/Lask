package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ObserveUserPreferencesUseCase(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<UserPreferences> =
        repository.observeUserPreferences()
}