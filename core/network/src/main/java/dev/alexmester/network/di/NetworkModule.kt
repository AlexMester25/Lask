package dev.alexmester.network.di

import dev.alexmester.network.plugin.ApiKeyPlugin
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin qualifier для API ключа.
 * Регистрируется в :app модуле через BuildConfig, чтобы core:network
 * не зависел от BuildConfig и оставался переиспользуемым.
 */
val newsApiKey = named("news_api_key")

val networkModule = module {

    single {
        val apiKey = get<String>(newsApiKey)

        HttpClient(Android) {

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }

            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.HEADERS
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 30_000
            }

            install(DefaultRequest) {
                url("https://api.worldnewsapi.com/")
                headers.append("Accept", "application/json")
            }

            install(ApiKeyPlugin) {
                this.apiKey = apiKey
            }

            install(HttpRequestRetry) {
                retryIf { _, response ->
                    response.status.value in 500..599
                }
                exponentialDelay(
                    base = 2.0,
                    maxDelayMs = 10_000,
                )
                maxRetries = 3
            }
        }
    }
}