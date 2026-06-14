package dev.alexmester.data.di

import dev.alexmester.data.repository.UserPreferencesRepositoryImpl
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.domain.usecase.ObserveUserPreferencesUseCase
import org.koin.dsl.module

val data = module {

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(preferencesDataSource = get())
    }

    factory { ObserveUserPreferencesUseCase(repository = get()) }
}