package dev.alexmester.impl.presentation.mvi

import androidx.compose.runtime.Composable

import androidx.compose.ui.res.stringResource
import dev.alexmester.domain.interests.InterestError
import dev.alexmester.domain.interests.InterestRules
import dev.alexmester.ui.R

data class InterestsState(
    val interests: Set<String> = emptySet(),
    val inputText: String = "",
) {
    val validationError: InterestError?
        get() = if (inputText.isBlank()) null else InterestRules.validate(inputText, interests)

    val canAdd: Boolean
        get() = InterestRules.validate(inputText, interests) == null
}

@Composable
fun InterestError.toUiString(): String = when (this) {
    InterestError.TooShort -> stringResource(R.string.interests_error_too_short)
    InterestError.TooLong -> stringResource(R.string.interests_error_too_long)
    InterestError.NoLetters -> stringResource(R.string.interests_error_no_letters)
    InterestError.InvalidCharacters -> stringResource(R.string.interests_error_invalid_chars)
    InterestError.AlreadyExists -> stringResource(R.string.interests_error_already_exists)
}

