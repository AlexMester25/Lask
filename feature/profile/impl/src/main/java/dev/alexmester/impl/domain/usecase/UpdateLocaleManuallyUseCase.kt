package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ProfileRepository

class UpdateLocaleManuallyUseCase(
    private val repository: ProfileRepository,
) {
    operator suspend fun invoke(country: String, language: String) =
        repository.updateLocaleManually(country,language)
}