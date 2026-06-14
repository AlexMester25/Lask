package dev.alexmester.impl.presentation.interests.mvi

sealed class InterestsIntent {
    data class OnInputChange(val text: String) : InterestsIntent()
    data object Add : InterestsIntent()
    data class Remove(val keyword: String) : InterestsIntent()
    data object Back : InterestsIntent()
}