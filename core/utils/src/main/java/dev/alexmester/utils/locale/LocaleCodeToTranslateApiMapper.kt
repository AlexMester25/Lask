package dev.alexmester.utils.locale

object LocaleCodeToTranslateApiMapper {
    private val corrections = mapOf(
        "zn" to "zh-CN",
    )

    fun mapToTranslateApiCode(code: String): String {
        return corrections[code.lowercase()] ?: code
    }
}