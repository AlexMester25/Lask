package dev.alexmester.network.di

import dev.alexmester.network.BuildConfig
import dev.alexmester.network.di.BaseClient.BASE_URL_TRANSLATE
import dev.alexmester.network.di.BaseClient.BASE_URL_WORLD_NEWS
import dev.alexmester.network.di.BaseClient.createBaseClient
import dev.alexmester.network.plugin.ApiKeyTranslatePlugin
import dev.alexmester.network.plugin.ApiKeyWorldNewsPlugin
import dev.alexmester.network.translate.TranslateApiService
import io.ktor.client.plugins.DefaultRequest
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class Clients {
    WORLD_NEWS,
    TRANSLATE,
}

val networkModule = module {
    single(named(Clients.WORLD_NEWS)) {
        createBaseClient().config {
            install(DefaultRequest) {
                url(BASE_URL_WORLD_NEWS)
                headers.append("Accept", "application/json")
            }
            install(ApiKeyWorldNewsPlugin) {
                apiKey = BuildConfig.NEWS_API_KEY
            }
        }
    }

    single(named(Clients.TRANSLATE)) {
        createBaseClient().config {
            install(DefaultRequest) {
                url(BASE_URL_TRANSLATE)
                headers.append("Accept", "application/json")
            }
            install(ApiKeyTranslatePlugin) {
                apiKey = BuildConfig.TRANSLATE_PLUS_API_KEY
            }
        }
    }

    single {
        TranslateApiService(
            client = get(named(Clients.TRANSLATE)),
        )
    }
}
