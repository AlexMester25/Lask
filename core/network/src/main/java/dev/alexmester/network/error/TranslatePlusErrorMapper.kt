package dev.alexmester.network.error

import dev.alexmester.models.error.NetworkError
import io.ktor.client.plugins.ResponseException

class TranslatePlusErrorMapper : BaseNetworkMapper() {
    override fun mapHttpError(throwable: ResponseException): NetworkError {
        if (throwable.response.status.value == 422) {
            return NetworkError.TranslateError()
        }
        return super.mapHttpError(throwable)
    }
}