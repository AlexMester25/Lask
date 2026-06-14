package dev.alexmester.impl.domain.model

import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.models.news.NewsCluster

data class FeedCombineData(
    val clusters: List<NewsCluster>,
    val preferences: UserPreferences
)
