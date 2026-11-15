package dev.alexmester.impl.domain.usecase

import dev.alexmester.domain.repository.ArticleTranslationRepository
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.translation.ArticleTranslation
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class TranslateTextsUseCase(
    private val repository: ArticleDetailRepository,
    private val translationRepository: ArticleTranslationRepository,
) {
    operator suspend fun invoke(
        articleId: Long,
        title: String,
        bodyText: String,
        targetLanguage: String,
        sourceLanguage: String,
    ): AppResult<ArticleTranslation> {
        translationRepository.getTranslation(articleId, targetLanguage)
            ?.let { return AppResult.Success(it) }

        return supervisorScope {
            val translatedTitle = async {
                repository.translateText(title, targetLanguage, sourceLanguage)
            }
            val translatedBody = async {
                repository.translateText(bodyText, targetLanguage, sourceLanguage)
            }
            val titleResult = translatedTitle.await()
            val bodyResult = translatedBody.await()

            when {
                titleResult is AppResult.Failure -> titleResult
                bodyResult is AppResult.Failure -> bodyResult
                titleResult is AppResult.Success && bodyResult is AppResult.Success -> {
                    val translation = ArticleTranslation(
                        title = titleResult.data,
                        text = bodyResult.data,
                    )
                    // Кэш — вспомогательная оптимизация: сбой записи не должен
                    // превращать уже полученный перевод в ошибку.
                    translationRepository.saveTranslation(
                        articleId = articleId,
                        targetLanguage = targetLanguage,
                        translation = translation,
                    )
                    AppResult.Success(translation)
                }

                else -> AppResult.Failure(NetworkError.Unknown())
            }
        }
    }
}
