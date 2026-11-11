package dev.alexmester.data.remote.source

import dev.alexmester.data.error.NetworkErrorMapper
import dev.alexmester.data.extension.safeApiCall
import dev.alexmester.data.remote.api.TranslateApiService
import dev.alexmester.domain.source.TranslateRemoteDataSource
import dev.alexmester.models.result.AppResult

internal class TranslateRemoteDataSourceImpl(
    private val api: TranslateApiService,
) : TranslateRemoteDataSource {

    private val errorMapper = NetworkErrorMapper.translatePlus()

    override suspend fun translate(
        text: String,
        sourceLanguage: String?,
        targetLanguage: String
    ): AppResult<String> = safeApiCall(errorMapper){
        api.translate(
            text = text,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage
        ).translations.translatedText
    }
}