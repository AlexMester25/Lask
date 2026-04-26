package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class TranslateTextsUseCase(
    private val repository: ArticleDetailRepository,
) {
    operator suspend fun invoke(
        title: String,
        bodyText: String,
        targetLanguage: String,
        sourceLanguage: String?,
    ): AppResult<Pair<String, String>> = supervisorScope {
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
                AppResult.Success(titleResult.data to bodyResult.data)
            }
            else -> AppResult.Failure(NetworkError.Unknown())
        }
    }
}