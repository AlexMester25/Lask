package dev.alexmester.domain.repository

import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun observeReadArticleIds(): Flow<List<Long>>
}