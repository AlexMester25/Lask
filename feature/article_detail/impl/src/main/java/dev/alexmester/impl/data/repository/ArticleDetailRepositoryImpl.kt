package dev.alexmester.impl.data.repository

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.mappers.toDomain
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.extension.safeApiCall
import dev.alexmester.network.translate.TranslateApiService
import dev.alexmester.platform.dispatchers.DispatcherProvider
import dev.alexmester.utils.constants.LaskConstants.DELIMITER_POINT
import dev.alexmester.utils.constants.LaskConstants.MAX_TRANSLATE_CHARS
import dev.alexmester.utils.constants.LaskConstants.TEXT_ECLIPSE
import dev.alexmester.utils.constants.LaskConstants.XP_PER_CLAP
import dev.alexmester.utils.constants.LaskConstants.XP_PER_READ
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ArticleDetailRepositoryImpl(
    private val local: ArticleDetailLocalDataSource,
    private val translateApiService: TranslateApiService,
    private val preferencesDataSource: UserPreferencesDataSource,
    private val dispatchers: DispatcherProvider,
) : ArticleDetailRepository {

    override suspend fun getArticleById(id: Long): NewsArticle? =
        withContext(dispatchers.io) {
            local.getArticleById(id)?.toDomain()
        }

    override fun observeIsBookmarked(id: Long): Flow<Boolean> =
        local.observeIsBookmarked(id)

    override suspend fun toggleBookmark(articleId: Long): Boolean =
        withContext(dispatchers.io) {
            local.toggleBookmark(articleId)
        }

    override fun observeClapCount(id: Long): Flow<Int> =
        local.observeClapCount(id)

    override suspend fun addClap(articleId: Long) =
        withContext(dispatchers.io) {
            local.addClap(articleId)
            preferencesDataSource.addXp(XP_PER_CLAP)
        }

    override suspend fun markAsRead(articleId: Long) =
        withContext(dispatchers.io) {
            local.markAsRead(articleId)
            preferencesDataSource.addXp(XP_PER_READ)
        }

    override suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String?,
    ): AppResult<String> = withContext(dispatchers.io) {
        safeApiCall {
            val truncated = if (text.length > MAX_TRANSLATE_CHARS) {
                text.take(MAX_TRANSLATE_CHARS)
                    .substringBeforeLast(
                        delimiter = DELIMITER_POINT,
                        missingDelimiterValue = text.take(MAX_TRANSLATE_CHARS)
                    ) + TEXT_ECLIPSE
            } else text
            val response = translateApiService.translate(
                text = truncated,
                targetLanguage = targetLanguage,
                sourceLanguage = sourceLanguage,
            )
            response.translations.translatedText
        }
    }

    override suspend fun getAutoTranslateLanguage(): String =
        withContext(dispatchers.io) {
            preferencesDataSource.userPreferences.first().autoTranslateLanguage
        }
}