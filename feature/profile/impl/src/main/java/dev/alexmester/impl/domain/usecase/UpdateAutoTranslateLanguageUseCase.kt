package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ProfileRepository

class UpdateAutoTranslateLanguageUseCase(
    private val repository: ProfileRepository,
) {
    operator suspend fun invoke(language: String) =
        repository.updateAutoTranslateLanguage(language)
}