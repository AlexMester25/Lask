package dev.alexmester.network.plugin

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.URLBuilder

/**
 * Ktor Client Plugin для автодобавления api-key query-параметра ко всем запросам.
 *
 */
val ApiKeyPlugin: ClientPlugin<ApiKeyConfig> = createClientPlugin(
    name = "ApiKeyPlugin",
    createConfiguration = ::ApiKeyConfig,
) {
    val apiKey = pluginConfig.apiKey

    onRequest { request, _ ->
        request.url.parameters.append("api-key", apiKey)
    }
}

class ApiKeyConfig {
    var apiKey: String = ""
}
