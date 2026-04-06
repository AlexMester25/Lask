package dev.alexmester.impl.presentation.mvi

object ArticleDetailReducer {

    fun reduce(
        state: ArticleDetailState,
        intent: ArticleDetailIntent,
    ): ArticleDetailState {
        val content = state.contentOrNull ?: return state
        return when (intent) {
            is ArticleDetailIntent.Clap -> content.copy(
                clapCount = content.clapCount + 1,
                isClapAnimating = true,
            )
            is ArticleDetailIntent.ToggleBookmark -> content.copy(
                isBookmarked = !content.isBookmarked,
            )
            else -> state
        }
    }

    fun onBookmarkUpdate(
        state: ArticleDetailState,
        isBookmarked: Boolean,
    ): ArticleDetailState =
        state.contentOrNull?.copy(isBookmarked = isBookmarked) ?: state

    fun onClapCountUpdated(
        state: ArticleDetailState,
        count: Int,
    ): ArticleDetailState =
        state.contentOrNull?.copy(clapCount = count) ?: state
}