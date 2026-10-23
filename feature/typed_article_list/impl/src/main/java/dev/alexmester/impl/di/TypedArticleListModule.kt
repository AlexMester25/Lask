package dev.alexmester.impl.di

import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val typedArticleList = module {
    viewModel { (type: ArticleListType) ->
        TypedArticleListViewModel(
            type = type,
            articleRepository = get(),
        )
    }
}