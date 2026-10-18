package dev.alexmester.impl.presentation.mvi

import dev.alexmester.ui.uitext.UiText

sealed interface ExploreSideEffect {
    data class ShowError(val message: UiText) : ExploreSideEffect
    data class ShowWarning(val message: UiText) : ExploreSideEffect
    data class NavigateToArticle(val articleId: Long, val articleUrl: String) : ExploreSideEffect
}