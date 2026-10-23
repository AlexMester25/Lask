package dev.alexmester.lask.splash_screen

import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val splash = module {
    viewModel<SplashViewModel>()
}