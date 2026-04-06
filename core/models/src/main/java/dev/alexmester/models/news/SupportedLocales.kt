package dev.alexmester.models.news

object SupportedLocales {

    val SUPPORTED_COUNTRIES = setOf(
        "us", "gb", "de", "fr", "it", "es", "ru", "cn", "jp", "au",
        "ca", "br", "in", "mx", "za", "ar", "kr", "nl", "se", "no",
        "dk", "fi", "pl", "cz", "at", "ch", "be", "pt", "gr", "hu",
        "ro", "bg", "hr", "sk", "si", "lt", "lv", "ee", "ua", "tr",
        "il", "sa", "ae", "eg", "ng", "ke", "gh", "pk", "id", "th",
        "vn", "ph", "my", "sg", "nz", "ie", "hk", "tw",
    )

    val SUPPORTED_LANGUAGES = setOf(
        "en", "de", "fr", "it", "es", "ru", "zh", "ja", "ar", "pt",
        "nl", "sv", "no", "da", "fi", "pl", "cs", "hu", "ro", "tr",
        "ko", "vi", "th", "id", "ms", "uk", "he", "fa",
    )

    const val FALLBACK_COUNTRY = "us"
    const val FALLBACK_LANGUAGE = "en"
}