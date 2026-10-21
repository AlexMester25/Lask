package dev.alexmester.impl.di

import dev.alexmester.impl.presentation.mvi.InterestsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val interests = module {

    viewModel { InterestsViewModel(userPreferencesRepository = get()) }
}