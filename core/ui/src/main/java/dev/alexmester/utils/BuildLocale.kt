package dev.alexmester.utils

import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.models.news.LanguageFlagMap
import dev.alexmester.models.news.SupportedLocales
import dev.alexmester.ui.components.locale.countryCodeToFlagEmoji
import java.util.Locale

object BuildLocale {

    fun countryCodeToFullCountryName(countryCode: String): String =
        Locale("", countryCode
            .uppercase()).getDisplayCountry(Locale.ENGLISH)
            .replaceFirstChar { it.uppercase() }

    fun buildCountryItems(): List<LocaleItem> =
        SupportedLocales.SUPPORTED_COUNTRIES
            .map { code ->
                val locale = Locale("", code.uppercase())
                LocaleItem(
                    code = code,
                    displayName = locale.getDisplayCountry(Locale.ENGLISH)
                        .replaceFirstChar { it.uppercase() },
                    flag = countryCodeToFlagEmoji(code),
                )
            }
            .sortedBy { it.displayName }

    fun buildLanguageItems(): List<LocaleItem> =
        SupportedLocales.SUPPORTED_LANGUAGES
            .map { code ->
                LocaleItem(
                    code = code,
                    displayName = Locale(code)
                        .getDisplayLanguage(Locale.ENGLISH)
                        .replaceFirstChar { it.uppercase() },
                    flag = countryCodeToFlagEmoji(LanguageFlagMap.flagCountryFor(code)),
                )
            }
            .sortedBy { it.displayName }
}