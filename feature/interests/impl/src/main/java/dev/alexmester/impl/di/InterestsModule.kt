package dev.alexmester.impl.di

import dev.alexmester.api.navigation.InterestsApi
import dev.alexmester.impl.navigation.InterestsImpl
import dev.alexmester.impl.presentation.mvi.InterestsViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val interests = module {
    single<InterestsImpl>() bind InterestsApi::class
    viewModel<InterestsViewModel>()
}