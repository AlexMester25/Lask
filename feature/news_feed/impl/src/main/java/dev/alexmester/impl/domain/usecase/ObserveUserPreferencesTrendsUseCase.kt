package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow

class ObserveUserPreferencesTrendsUseCase(
    private val repository: NewsFeedRepository
) {
    operator fun invoke(): Flow<UserPreferences> =
        repository.observeUserPreferences()
}