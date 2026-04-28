package dev.alexmester.impl.domain.usecase

import android.net.Uri
import dev.alexmester.impl.domain.repository.ProfileRepository

class ApplyEditChangesUseCase(
    private val repository: ProfileRepository,
) {
    operator suspend fun invoke(
        imageUri: Uri?,
        name: String
    ) = repository.applyEditChanges(imageUri,name)
}