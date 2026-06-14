package dev.alexmester.data.repository

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl(
    private val preferencesDataSource: UserPreferencesDataSource
) : UserPreferencesRepository {

    override fun observeUserPreferences(): Flow<UserPreferences> =
        preferencesDataSource.userPreferences
}