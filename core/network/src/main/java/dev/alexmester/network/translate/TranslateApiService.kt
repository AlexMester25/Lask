package dev.alexmester.network.translate

import dev.alexmester.network.endpoints.ApiRoutes
import dev.alexmester.network.translate.dto.TranslateDataDto
import dev.alexmester.network.translate.dto.TranslateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TranslateApiService(
    private val client: HttpClient,
) {
    /**
     * Переводит текст через TranslatePlus v2 API.
     * Документация: https://docs.translateplus.io/reference/v2/translation/translate
     */
    suspend fun translate(
        text: String,
        sourceLanguage: String? = null,
        targetLanguage: String,
    ): TranslateDataDto =
        client.post(ApiRoutes.Translate.TRANSLATE) {
            contentType(ContentType.Application.Json)
            setBody(
                TranslateRequestDto(
                    text = text,
                    source = sourceLanguage,
                    target = targetLanguage,
                )
            )
        }.body()
}