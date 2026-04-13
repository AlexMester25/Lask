package dev.alexmester.lask.di

import dev.alexmester.datastore.util.DeviceLocaleProvider
import dev.alexmester.datastore.util.LocaleChangeObserver
import dev.alexmester.lask.welcome_screen.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel {
        SplashViewModel(
            preferencesDataSource = get(),
            deviceLocaleProvider = get(),
        )
    }
    single { DeviceLocaleProvider(androidContext()) }
    single {
        LocaleChangeObserver(
            context = androidContext(),
            deviceLocaleProvider = get(),
            preferencesDataSource = get(),
            scope = get<CoroutineScope>(),
        )
    }
}