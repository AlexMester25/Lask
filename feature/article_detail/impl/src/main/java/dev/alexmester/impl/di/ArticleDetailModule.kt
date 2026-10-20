package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.ArticleDetailRepositoryImpl
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.domain.usecase.TranslateTextsUseCase
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val articleDetailModule = module {


    single<ArticleDetailRepository> {
        ArticleDetailRepositoryImpl(
            articleRepository = get(),
            translateApiService = get(),
            preferencesDataSource = get(),
            dispatchers = get()
        )
    }

    factory { TranslateTextsUseCase(repository = get()) }

    viewModel { (articleId: Long, articleUrl: String) ->
        ArticleDetailViewModel(
            articleId = articleId,
            articleUrl = articleUrl,
            articleRepository = get(),
            articleDetailRepository = get(),
            translateTextsUseCase = get(),
        )
    }
}