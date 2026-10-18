package dev.alexmester.data.di

import dev.alexmester.data.repository.ArticleRepositoryImpl
import dev.alexmester.data.repository.UserPreferencesRepositoryImpl
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository
import org.koin.dsl.module

val data = module {

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(preferencesDataSource = get())
    }
    single<ArticleRepository> {
        ArticleRepositoryImpl(userStateDao = get())
    }

}