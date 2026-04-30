package dev.alexmester.platform.dispatchers

import org.koin.dsl.module

val platformModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}