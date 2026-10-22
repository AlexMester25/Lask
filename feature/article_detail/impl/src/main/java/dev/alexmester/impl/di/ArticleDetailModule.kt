package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.ArticleDetailRepositoryImpl
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.domain.usecase.ClapArticleUseCase
import dev.alexmester.impl.domain.usecase.MarkArticleAsReadUseCase
import dev.alexmester.impl.domain.usecase.TranslateTextsUseCase
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val articleDetail = module {

    factory<MarkArticleAsReadUseCase>()
    factory<ClapArticleUseCase>()
    factory<TranslateTextsUseCase>()
    single<ArticleDetailRepositoryImpl>() bind ArticleDetailRepository::class
    viewModel<ArticleDetailViewModel>()
}