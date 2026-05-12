package dev.alexmester.impl.domain.repository

import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {

    fun observeFeedClusters(): Flow<List<NewsCluster>>

    fun observeReadArticleIds(): Flow<List<Long>>

    fun observeUserPreferences(): Flow<UserPreferences>

    suspend fun refreshFeed(force: Boolean): AppResult<RefreshFeedResult>

    suspend fun getLastCachedAt(): Long?
}