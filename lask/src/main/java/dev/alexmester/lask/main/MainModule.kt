package dev.alexmester.lask.main

import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val mainModule = module {
    viewModel<MainViewModel>()
}