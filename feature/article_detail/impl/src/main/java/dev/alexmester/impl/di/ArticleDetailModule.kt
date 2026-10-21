package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.ArticleDetailRepositoryImpl
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.domain.usecase.ClapArticleUseCase
import dev.alexmester.impl.domain.usecase.MarkArticleAsReadUseCase
import dev.alexmester.impl.domain.usecase.TranslateTextsUseCase
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val articleDetailModule = module {

    factory { MarkArticleAsReadUseCase(articleRepository = get(), userPreferencesRepository = get()) }
    factory { ClapArticleUseCase(articleRepository = get(), userPreferencesRepository = get()) }

    single<ArticleDetailRepository> {
        ArticleDetailRepositoryImpl(
            translateApiService = get(),
            userPreferencesRepository  = get(),
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
            markArticleAsReadUseCase = get(),
            clapArticleUseCase = get(),
        )
    }
}