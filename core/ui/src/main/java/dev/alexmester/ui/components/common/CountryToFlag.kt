package dev.alexmester.ui.components.common

fun countryToFlag(countryCode: String): String {
    return countryCode
        .uppercase()
        .map { char -> Character.toCodePoint('\uD83C', '\uDDE6' + (char - 'A')) }
        .joinToString("") { String(Character.toChars(it)) }
}