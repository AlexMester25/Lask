package dev.alexmester.impl.data.repository

import dev.alexmester.domain.source.TranslateRemoteDataSource
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.result.AppResult
import dev.alexmester.utils.constants.LaskConstants.DELIMITER_POINT
import dev.alexmester.utils.constants.LaskConstants.MAX_TRANSLATE_CHARS
import dev.alexmester.utils.constants.LaskConstants.TEXT_ECLIPSE
import dev.alexmester.utils.locale.LocaleCodeToTranslateApiMapper

class ArticleDetailRepositoryImpl(
    private val translateRemoteDataSource: TranslateRemoteDataSource,
) : ArticleDetailRepository {

    override suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String,
    ): AppResult<String> {
        val truncated = if (text.length > MAX_TRANSLATE_CHARS) {
            text.take(MAX_TRANSLATE_CHARS)
                .substringBeforeLast(
                    delimiter = DELIMITER_POINT,
                    missingDelimiterValue = text.take(MAX_TRANSLATE_CHARS)
                ) + TEXT_ECLIPSE
        } else text
        val correctedSourceLang =
            LocaleCodeToTranslateApiMapper.mapToTranslateApiCode(sourceLanguage)
        val correctedTargetLang =
            LocaleCodeToTranslateApiMapper.mapToTranslateApiCode(targetLanguage)
        val response = translateRemoteDataSource.translate(
            text = truncated,
            sourceLanguage = correctedSourceLang,
            targetLanguage = correctedTargetLang,
        )
        return response
    }

}