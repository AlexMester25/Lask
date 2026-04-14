package dev.alexmester.ui.components.locale

fun countryCodeToFlagEmoji(countryCode: String): String {
    return countryCode
        .uppercase()
        .map { char -> Character.toCodePoint('\uD83C', '\uDDE6' + (char - 'A')) }
        .joinToString("") { String(Character.toChars(it)) }
}