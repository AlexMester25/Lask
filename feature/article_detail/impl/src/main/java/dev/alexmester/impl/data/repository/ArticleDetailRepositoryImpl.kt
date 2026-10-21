package dev.alexmester.impl.data.repository

import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.error.TranslatePlusErrorMapper
import dev.alexmester.network.extension.safeApiCall
import dev.alexmester.network.translate.TranslateApiService
import dev.alexmester.platform.dispatchers.DispatcherProvider
import dev.alexmester.utils.constants.LaskConstants.DELIMITER_POINT
import dev.alexmester.utils.constants.LaskConstants.MAX_TRANSLATE_CHARS
import dev.alexmester.utils.constants.LaskConstants.TEXT_ECLIPSE
import dev.alexmester.utils.constants.LaskConstants.XP_PER_CLAP
import dev.alexmester.utils.constants.LaskConstants.XP_PER_READ
import dev.alexmester.utils.locale.LocaleCodeToTranslateApiMapper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ArticleDetailRepositoryImpl(
    private val articleRepository: ArticleRepository,
    private val translateApiService: TranslateApiService,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dispatchers: DispatcherProvider,
) : ArticleDetailRepository {

    private val errorMapper = TranslatePlusErrorMapper()

    override suspend fun addClap(articleId: Long) {
        articleRepository.addClap(articleId)
        userPreferencesRepository.addXp(XP_PER_CLAP)
    }

    override suspend fun markAsRead(articleId: Long) {
        articleRepository.markAsRead(articleId)
        userPreferencesRepository.addXp(XP_PER_READ)
    }

    override suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String,
    ): AppResult<String> = withContext(dispatchers.io) {
        safeApiCall(errorMapper) {
            val truncated = if (text.length > MAX_TRANSLATE_CHARS) {
                text.take(MAX_TRANSLATE_CHARS)
                    .substringBeforeLast(
                        delimiter = DELIMITER_POINT,
                        missingDelimiterValue = text.take(MAX_TRANSLATE_CHARS)
                    ) + TEXT_ECLIPSE
            } else text
            val correctedSourceLang = LocaleCodeToTranslateApiMapper.mapToTranslateApiCode(sourceLanguage)
            val correctedTargetLang = LocaleCodeToTranslateApiMapper.mapToTranslateApiCode(targetLanguage)
            val response = translateApiService.translate(
                text = truncated,
                sourceLanguage = correctedSourceLang,
                targetLanguage = correctedTargetLang,
            )
            response.translations.translatedText
        }
    }

    override suspend fun getAutoTranslateLanguage(): String =
        withContext(dispatchers.io) {
            userPreferencesRepository.observeUserPreferences().first().autoTranslateLanguage
        }
}