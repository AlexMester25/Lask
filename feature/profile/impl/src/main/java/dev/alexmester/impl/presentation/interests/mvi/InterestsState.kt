package dev.alexmester.impl.presentation.interests.mvi

import androidx.compose.runtime.Composable

import androidx.compose.ui.res.stringResource
import dev.alexmester.ui.R

data class InterestsState(
    val interests: Set<String> = emptySet(),
    val inputText: String = "",
) {
    val trimmed: String get() = inputText.trim().lowercase()

    val validationError: InterestsValidationError?
        get() {
            if (trimmed.isBlank()) return null
            return when {
                trimmed.length < 2 -> InterestsValidationError.TooShort
                trimmed.length > 30 -> InterestsValidationError.TooLong
                !trimmed.any { it.isLetter() } -> InterestsValidationError.NoLetters
                !VALID_KEYWORD_REGEX.matches(trimmed) -> InterestsValidationError.InvalidCharacters
                trimmed in interests -> InterestsValidationError.AlreadyExists
                else -> null
            }
        }

    val canAdd: Boolean
        get() = trimmed.isNotBlank() && validationError == null

    companion object {
        private val VALID_KEYWORD_REGEX = Regex("^[\\p{L}\\p{N} \\-]+$")
    }
}

enum class InterestsValidationError {
    TooShort,
    TooLong,
    NoLetters,
    InvalidCharacters,
    AlreadyExists,
}

@Composable
fun InterestsValidationError.toUiString(): String = when (this) {
    InterestsValidationError.TooShort -> stringResource(R.string.interests_error_too_short)
    InterestsValidationError.TooLong -> stringResource(R.string.interests_error_too_long)
    InterestsValidationError.NoLetters -> stringResource(R.string.interests_error_no_letters)
    InterestsValidationError.InvalidCharacters -> stringResource(R.string.interests_error_invalid_chars)
    InterestsValidationError.AlreadyExists -> stringResource(R.string.interests_error_already_exists)
}

