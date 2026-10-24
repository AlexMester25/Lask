package dev.alexmester.impl.di

import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.api.navigation.TypedArticleListApi
import dev.alexmester.impl.navigation.TypedArticleListImpl
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val typedArticleList = module {
    single<TypedArticleListImpl>() bind TypedArticleListApi::class
    viewModel { (type: ArticleListType) ->
        TypedArticleListViewModel(
            type = type,
            articleRepository = get(),
        )
    }
}