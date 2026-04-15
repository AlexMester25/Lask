package dev.alexmester.impl.presentstion.mvi

sealed interface ExploreIntent {
    data object Refresh : ExploreIntent
    data object LoadMore : ExploreIntent
    data class ArticleClick(val articleId: Long, val articleUrl: String) : ExploreIntent
}