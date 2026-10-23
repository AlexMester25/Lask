package dev.alexmester.impl.di

import dev.alexmester.impl.presentation.mvi.InterestsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val interests = module {
    viewModel<InterestsViewModel>()
}