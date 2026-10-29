package dev.alexmester.impl.presentation.components.body

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.components.avatars.AuthorAvatar
import dev.alexmester.ui.components.sentiment.LaskSentimentGauge
import dev.alexmester.ui.components.sentiment.SentimentText
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.utils.date.DateUtils

@Composable
internal fun ArticleDetailAuthorRow(
    authors: List<String>,
    publishDate: String,
    sentiment: Double?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        sentiment?.let { sentiment ->
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                LaskSentimentGauge(
                    modifier = Modifier.width(60.dp),
                    sentiment = sentiment,
                    arcStrokeWidth = 6.dp,
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    SentimentText(
                        modifier = Modifier,
                        sentiment = sentiment
                    )
                    Text(
                        text = stringResource(R.string.sentiment_title),
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.textSecondary,
                    )
                }
            }
        }
        VerticalDivider(
            modifier = Modifier.height(24.dp),
            color = MaterialTheme.LaskColors.textSecondary
        )
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            AuthorAvatar(
                modifier = Modifier.size(48.dp),
                char = authors.firstOrNull()?.firstOrNull()?.toString() ?: "?",
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = authors.firstOrNull() ?: stringResource(R.string.unknown_author),
                    style = MaterialTheme.LaskTypography.body2SemiBold,
                    color = MaterialTheme.LaskColors.textPrimary,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = DateUtils.formatPublishDate(publishDate),
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}