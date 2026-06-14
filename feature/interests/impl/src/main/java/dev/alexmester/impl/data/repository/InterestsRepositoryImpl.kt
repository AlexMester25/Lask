package dev.alexmester.impl.data.repository

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.impl.domain.repository.InterestsRepository
import kotlinx.coroutines.flow.Flow

class InterestsRepositoryImpl(
    private val preferencesDataSource: UserPreferencesDataSource,
) : InterestsRepository {

    override suspend fun addInterest(keyWord: String) =
        preferencesDataSource.addInterest(keyWord)

    override suspend fun removeInterest(keyWord: String) =
        preferencesDataSource.removeInterest(keyWord)
}