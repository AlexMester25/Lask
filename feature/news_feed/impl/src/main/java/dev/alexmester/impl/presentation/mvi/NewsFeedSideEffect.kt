package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.error.AppError
import dev.alexmester.ui.uitext.UiText


sealed class NewsFeedSideEffect {

    data class NavigateToArticle(
        val articleId: Long,
        val articleUrl: String,
    ) : NewsFeedSideEffect()

    data class ShowError(val message: AppError) : NewsFeedSideEffect()
    data class ShowWarning(val message: UiText) : NewsFeedSideEffect()
}
