package dev.alexmester.lask.theme_switch

import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val themeSwitch = module {
    viewModel<ThemeViewModel>()
}