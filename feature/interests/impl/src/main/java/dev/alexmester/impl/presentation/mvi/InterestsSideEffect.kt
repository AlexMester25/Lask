package dev.alexmester.impl.presentation.mvi

sealed class InterestsSideEffect {
    data object NavigateBack : InterestsSideEffect()
}