package dev.alexmester.ui.components.list_card.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.sentiment.LaskSentimentDot
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.utils.date.DateUtils
import dev.alexmester.utils.locale.LocaleUtils
import dev.alexmester.utils.locale.LocaleUtils.countryCodeToFlagEmoji

@Composable
internal fun ArticleMeta(article: NewsArticle) {
    val countryCode = article.sourceCountry
    val flagEmoji = remember(countryCode) {
        countryCode?.let { countryCodeToFlagEmoji(it) }
    }
    val fullCountryName = remember(countryCode) {
        countryCode?.let { LocaleUtils.countryCodeToFullCountryName(it) }
    }
    val formattedCategory = remember(article.category) {
        article.category?.replaceFirstChar { c -> c.uppercase() }
    }
    val formattedDate = remember(article.publishDate) {
        DateUtils.formatPublishDate(article.publishDate)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (flagEmoji != null && fullCountryName != null) {
                Text(
                    text = flagEmoji,
                    style = MaterialTheme.LaskTypography.footnote
                )
                Text(
                    text = fullCountryName,
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            formattedCategory?.let {
                Text(
                    text = it,
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.informative,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            article.sentiment?.let { LaskSentimentDot(it) }
            Text(
                text = formattedDate,
                style = MaterialTheme.LaskTypography.footnote,
                color = MaterialTheme.LaskColors.textSecondary,
            )
            article.authors.firstOrNull()?.let { author ->
                Text(
                    text = "· $author",
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}