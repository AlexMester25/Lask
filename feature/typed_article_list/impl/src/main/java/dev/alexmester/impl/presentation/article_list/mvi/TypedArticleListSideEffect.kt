package dev.alexmester.impl.presentation.article_list.mvi

sealed class TypedArticleListSideEffect {
    data object NavigateBack: TypedArticleListSideEffect()
    data class NavigateToArticle(
        val articleId: Long,
        val articleUrl: String
    ): TypedArticleListSideEffect()
}