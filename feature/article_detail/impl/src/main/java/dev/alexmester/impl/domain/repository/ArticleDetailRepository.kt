package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.flow.Flow

interface ArticleDetailRepository {

    suspend fun getArticleById(id: Long): NewsArticle?

    fun observeIsBookmarked(id: Long): Flow<Boolean>

    fun observeClapCount(id: Long): Flow<Int>

    suspend fun toggleBookmark(articleId: Long): Boolean

    suspend fun addClap(articleId: Long)

    suspend fun markAsRead(articleId: Long)

    suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String? = null,
    ): AppResult<String>

    suspend fun getAutoTranslateLanguage(): String
}