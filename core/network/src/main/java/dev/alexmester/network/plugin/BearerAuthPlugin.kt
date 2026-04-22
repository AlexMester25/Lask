package dev.alexmester.network.plugin

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin

val BearerAuthPlugin: ClientPlugin<BearerAuthConfig> = createClientPlugin(
    name = "BearerAuthPlugin",
    createConfiguration = ::BearerAuthConfig,
) {
    val token = pluginConfig.token

    onRequest { request, _ ->
        request.headers.append("X-API-KEY", token)
    }
}

class BearerAuthConfig {
    var token: String = ""
}