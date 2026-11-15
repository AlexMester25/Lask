package dev.alexmester.domain.repository

import dev.alexmester.models.result.AppResult
import dev.alexmester.models.translation.ArticleTranslation

/**
 * Локальный кэш переводов статей.
 *
 * Ключ — пара «статья + целевой язык», поэтому переключение языка не затирает
 * ранее сохранённый перевод. Записи удаляются каскадно вместе со статьёй.
 */
interface ArticleTranslationRepository {

    suspend fun getTranslation(
        articleId: Long,
        targetLanguage: String,
    ): ArticleTranslation?

    /** Сохраняет перевод. Сбой хранилища возвращается как [AppResult.Failure]. */
    suspend fun saveTranslation(
        articleId: Long,
        targetLanguage: String,
        translation: ArticleTranslation,
    ): AppResult<Unit>
}
