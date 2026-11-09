package dev.alexmester.network.error

import dev.alexmester.models.error.NetworkError

private const val STATUS_UNPROCESSABLE_ENTITY = 422

class TranslatePlusErrorMapper : BaseNetworkMapper() {

    internal override fun mapHttpStatus(
        status: Int,
        description: String?,
        retryAfter: String?,
    ): NetworkError = if (status == STATUS_UNPROCESSABLE_ENTITY) {
        NetworkError.TranslateError()
    } else {
        super.mapHttpStatus(status, description, retryAfter)
    }
}
