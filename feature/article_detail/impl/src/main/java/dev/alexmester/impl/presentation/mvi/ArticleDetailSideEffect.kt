package dev.alexmester.impl.presentation.mvi

import dev.alexmester.ui.uitext.UiText

sealed class ArticleDetailSideEffect {
    data object NavigateBack : ArticleDetailSideEffect()
    data class ShareUrl(val url: String) : ArticleDetailSideEffect()
    data class ShowSnackbar(val isBookmarked: Boolean) : ArticleDetailSideEffect()
}