package dev.alexmester.platform.dispatchers

import org.koin.dsl.module

val platform = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}