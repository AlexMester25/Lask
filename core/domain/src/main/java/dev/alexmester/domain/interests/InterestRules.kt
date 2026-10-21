package dev.alexmester.domain.interests

enum class InterestError { TooShort, TooLong, NoLetters, InvalidCharacters, AlreadyExists }

object InterestRules {
    const val MIN_LENGTH = 2
    const val MAX_LENGTH = 30
    const val MAX_COUNT = 20

    private val ALLOWED_CHARS = Regex("^[\\p{L}\\p{N} \\-]+$")

    fun normalize(raw: String): String = raw.trim().lowercase()

    /** null означает, что слово можно добавить. Пустой ввод даёт [InterestError.TooShort]. */
    fun validate(raw: String, existing: Set<String>): InterestError? {
        val keyword = normalize(raw)
        return when {
            keyword.length < MIN_LENGTH -> InterestError.TooShort
            keyword.length > MAX_LENGTH -> InterestError.TooLong
            keyword.none { it.isLetter() } -> InterestError.NoLetters
            !ALLOWED_CHARS.matches(keyword) -> InterestError.InvalidCharacters
            keyword in existing -> InterestError.AlreadyExists
            else -> null
        }
    }
}