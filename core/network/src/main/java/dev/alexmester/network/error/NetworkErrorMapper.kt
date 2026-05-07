package dev.alexmester.network.error

import dev.alexmester.models.error.NetworkError

interface NetworkErrorMapper {
    fun map(throwable: Throwable): NetworkError
}