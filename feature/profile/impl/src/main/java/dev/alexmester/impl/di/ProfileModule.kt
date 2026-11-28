package dev.alexmester.impl.di

import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.impl.navigation.ProfileImpl
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val profileModule = module {
    single<ProfileImpl>() bind ProfileApi::class
    viewModel<ProfileViewModel>()
}
