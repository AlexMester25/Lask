package dev.alexmester.impl.domain.usecase

import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.impl.domain.model.FeedCombineData
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveTrendsUseCase(
    private val newsFeedRepository: NewsFeedRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<FeedCombineData> =
        newsFeedRepository.observeFeedClusters()
            .combine(userPreferencesRepository.observeUserPreferences()) { clusters, preferences ->
                FeedCombineData(
                    clusters = clusters,
                    preferences = preferences
                )
            }
}
