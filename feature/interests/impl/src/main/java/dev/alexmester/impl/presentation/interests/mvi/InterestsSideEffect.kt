package dev.alexmester.impl.presentation.interests.mvi

sealed class InterestsSideEffect {
    data object NavigateBack : InterestsSideEffect()
}