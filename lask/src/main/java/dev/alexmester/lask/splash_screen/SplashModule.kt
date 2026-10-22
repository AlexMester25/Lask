package dev.alexmester.lask.splash_screen

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val splash = module {
    viewModel {
        SplashViewModel(
            userPreferencesRepository = get(),
            localeSynchronizer = get(),
        )
    }
}