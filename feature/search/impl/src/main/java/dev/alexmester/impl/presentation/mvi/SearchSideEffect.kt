package dev.alexmester.impl.presentation.mvi

import dev.alexmester.ui.uitext.UiText

sealed class SearchSideEffect {
    data object NavigateBack : SearchSideEffect()
    data class ShowError(val message: UiText): SearchSideEffect()
    data class NavigateToArticle(val articleId: Long, val articleUrl: String) : SearchSideEffect()
}