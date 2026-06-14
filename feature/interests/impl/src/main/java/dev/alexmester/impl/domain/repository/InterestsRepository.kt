package dev.alexmester.impl.domain.repository

import dev.alexmester.models.preference.UserPreferences
import kotlinx.coroutines.flow.Flow

interface InterestsRepository {
//    fun observeUserPreferences(): Flow<UserPreferences>
    suspend fun addInterest(keyWord: String)
    suspend fun removeInterest(keyWord: String)
}