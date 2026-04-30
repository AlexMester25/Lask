package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.model.FeedCombineData
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveTrendsUseCase(
    private val repository: NewsFeedRepository,
) {
    operator fun invoke(): Flow<FeedCombineData> =
        repository.observeFeedClusters()
            .combine(repository.observeUserPreferences()) { clusters, preferences ->
                FeedCombineData(
                    clusters = clusters,
                    preferences = preferences
                )
            }
}
