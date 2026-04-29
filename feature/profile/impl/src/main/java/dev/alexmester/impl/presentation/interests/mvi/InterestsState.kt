package dev.alexmester.impl.presentation.interests.mvi

data class InterestsState(
    val interests: Set<String> = emptySet(),
    val inputText: String = "",
) {
    val canAdd: Boolean
        get() = inputText.trim().isNotBlank() &&
                inputText.trim() !in interests
}