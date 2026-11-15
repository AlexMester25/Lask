package dev.alexmester.data.repository

import dev.alexmester.data.extension.runStorageResult
import dev.alexmester.data.mapper.toDomain
import dev.alexmester.data.mapper.toEntity
import dev.alexmester.database.dao.ArticleTranslationDao
import dev.alexmester.domain.repository.ArticleTranslationRepository
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.translation.ArticleTranslation
import dev.alexmester.platform.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext

internal class ArticleTranslationRepositoryImpl(
    private val translationDao: ArticleTranslationDao,
    private val dispatchers: DispatcherProvider,
) : ArticleTranslationRepository {

    override suspend fun getTranslation(
        articleId: Long,
        targetLanguage: String,
    ): ArticleTranslation? = withContext(dispatchers.io) {
        translationDao.getTranslation(articleId, targetLanguage)?.toDomain()
    }

    override suspend fun saveTranslation(
        articleId: Long,
        targetLanguage: String,
        translation: ArticleTranslation,
    ): AppResult<Unit> = runStorageResult {
        withContext(dispatchers.io) {
            translationDao.upsertTranslation(
                translation.toEntity(articleId, targetLanguage)
            )
        }
    }
}
