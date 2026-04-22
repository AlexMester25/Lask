package dev.alexmester.network.translate.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslateRequestDto(
    val text: String,
    val source: String? = null,
    val target: String,
)

@Serializable
data class TranslateDataDto(
    val translations: TranslationDto,
)

@Serializable
data class TranslationDto(
    @SerialName("text") val originalText: String,
    @SerialName("translation") val translatedText: String,
    @SerialName("source") val sourceLanguage: String? = null,
    @SerialName("target") val targetLanguage: String? = null,
)