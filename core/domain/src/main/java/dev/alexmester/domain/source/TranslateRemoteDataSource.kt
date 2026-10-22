package dev.alexmester.domain.source

import dev.alexmester.models.result.AppResult

interface TranslateRemoteDataSource {
    suspend fun translate(
        text: String,
        sourceLanguage: String? = null,
        targetLanguage: String,
    ): AppResult<String>
}