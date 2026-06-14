package dev.alexmester.impl.presentation.article_list.mvi

sealed class TypedArticleListIntent {
    data class SelectCategory(val category: String?) : TypedArticleListIntent()
    data object Back : TypedArticleListIntent()
    data class ArticleClick(val articleId: Long, val articleUrl: String) : TypedArticleListIntent()
}