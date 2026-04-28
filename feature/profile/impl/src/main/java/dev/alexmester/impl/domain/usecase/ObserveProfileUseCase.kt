package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ObserveProfileUseCase(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<Pair<UserPreferences, Int>> =
        repository.observeProfile()
}