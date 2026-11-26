package dev.alexmester.impl.presentation.article_list.mvi

import dev.alexmester.domain.articles.CategoryFilter

sealed class TypedArticleListIntent {
    data class SelectFilter(val filter: CategoryFilter) : TypedArticleListIntent()
    data object LoadMore : TypedArticleListIntent()
    data object Back : TypedArticleListIntent()
    data class ArticleClick(val articleId: Long, val articleUrl: String) : TypedArticleListIntent()
}
