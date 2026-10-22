package dev.alexmester.impl.domain.repository

import dev.alexmester.models.result.AppResult

interface ArticleDetailRepository {
    suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String,
    ): AppResult<String>
}