package dev.alexmester.domain.repository

import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeUserPreferences(): Flow<UserPreferences>
}