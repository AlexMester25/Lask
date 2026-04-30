package dev.alexmester.impl.domain.model

import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.models.news.NewsCluster

data class FeedCombineData(
    val clusters: List<NewsCluster>,
    val preferences: UserPreferences
)
