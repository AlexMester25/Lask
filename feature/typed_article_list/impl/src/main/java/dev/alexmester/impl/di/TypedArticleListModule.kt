package dev.alexmester.impl.di

import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.impl.data.repository.TypedArticleListRepositoryImpl
import dev.alexmester.impl.domain.repository.TypedArticleListRepository
import dev.alexmester.impl.domain.usecase.ObserveClappedArticlesUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticlesUseCase
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val typedArticleList = module {

    single<TypedArticleListRepository> {
        TypedArticleListRepositoryImpl(
            userStateDao = get()
        )
    }

    factory { ObserveReadArticlesUseCase(repository = get()) }
    factory { ObserveClappedArticlesUseCase(repository = get()) }

    viewModel { (type: ArticleListType) ->
        TypedArticleListViewModel(
            type = type,
            observeReadArticlesUseCase = get(),
            observeClappedArticlesUseCase = get(),
        )
    }
}