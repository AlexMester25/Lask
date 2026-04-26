package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ArticleDetailRepository

class GetAutoTranslateLanguageUseCase(
    private val repository: ArticleDetailRepository,
) {
    operator suspend fun invoke(): String = repository.getAutoTranslateLanguage()
}