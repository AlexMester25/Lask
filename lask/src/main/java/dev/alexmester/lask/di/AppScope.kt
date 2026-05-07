package dev.alexmester.lask.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val appScope = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}