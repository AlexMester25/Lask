package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.repository.ArticleDetailRepositoryImpl
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.domain.usecase.TranslateTextsUseCase
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val articleDetailModule = module {

    single {
        ArticleDetailLocalDataSource(
            articleDao = get(),
            userStateDao = get(),
        )
    }

    single<ArticleDetailRepository> {
        ArticleDetailRepositoryImpl(
            local = get(),
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
            articleDetailRepository = get(),
            translateTextsUseCase = get(),
        )
    }
}