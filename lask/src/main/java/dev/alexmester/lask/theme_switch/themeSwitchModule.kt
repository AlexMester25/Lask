package dev.alexmester.lask.theme_switch

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val themeSwitchModule = module {
    viewModel {
        ThemeViewModel(preferencesDataSource = get(),)
    }
}