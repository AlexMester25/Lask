package dev.alexmester.testing.fake

import dev.alexmester.domain.repository.ArticleTranslationRepository
import dev.alexmester.models.error.AppError
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.translation.ArticleTranslation

/** Запись о сохранении перевода, накопленная фейком. */
data class TranslationCacheWrite(
    val articleId: Long,
    val targetLanguage: String,
    val translation: ArticleTranslation,
)

/**
 * In-memory [ArticleTranslationRepository] для тестов кэша переводов.
 */
class FakeArticleTranslationRepository : ArticleTranslationRepository {

    /** Значение, которое вернёт [getTranslation]; `null` — кэш пуст. */
    var cached: ArticleTranslation? = null

    /** Если задано, [saveTranslation] вернёт [AppResult.Failure]. */
    var saveFailure: AppError? = null

    val saved = mutableListOf<TranslationCacheWrite>()

    override suspend fun getTranslation(
        articleId: Long,
        targetLanguage: String,
    ): ArticleTranslation? = cached

    override suspend fun saveTranslation(
        articleId: Long,
        targetLanguage: String,
        translation: ArticleTranslation,
    ): AppResult<Unit> {
        saveFailure?.let { return AppResult.Failure(it) }
        saved += TranslationCacheWrite(articleId, targetLanguage, translation)
        return AppResult.Success(Unit)
    }
}
