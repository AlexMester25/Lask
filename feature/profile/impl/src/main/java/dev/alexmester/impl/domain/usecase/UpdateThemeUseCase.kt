package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ProfileRepository

class UpdateThemeUseCase(
    private val repository: ProfileRepository,
) {
    operator suspend fun invoke(isDark: Boolean?) =
        repository.updateTheme(isDark)
}