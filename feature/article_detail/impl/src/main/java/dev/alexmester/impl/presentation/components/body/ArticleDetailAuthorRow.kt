package dev.alexmester.impl.presentation.components.body

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alexmester.ui.R
import dev.alexmester.ui.components.avatars.AuthorAvatar
import dev.alexmester.ui.components.sentiment.LaskSentimentGauge
import dev.alexmester.ui.components.sentiment.SentimentText
import dev.alexmester.ui.design_system.LaskColors
import dev.alexmester.ui.design_system.LaskTypography
import dev.alexmester.utils.date.DateUtils

/**
 * Ширина строки метаданных фиксирована, поэтому шрифт подстраивается под доступное место:
 * имя автора и дата всегда остаются в одну строку и не переносятся на вторую.
 */
private val AuthorNameAutoSize = TextAutoSize.StepBased(
    minFontSize = 10.sp,
    maxFontSize = 14.sp,
    stepSize = 0.5.sp,
)

private val MetaAutoSize = TextAutoSize.StepBased(
    minFontSize = 9.sp,
    maxFontSize = 12.sp,
    stepSize = 0.5.sp,
)

@Composable
internal fun ArticleDetailAuthorRow(
    authors: List<String>,
    publishDate: String,
    sentiment: Double?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        sentiment?.let { sentiment ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                LaskSentimentGauge(
                    modifier = Modifier.width(48.dp),
                    sentiment = sentiment,
                    arcStrokeWidth = 6.dp,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    SentimentText(sentiment = sentiment)
                    Text(
                        text = stringResource(R.string.sentiment_title),
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        autoSize = MetaAutoSize,
                    )
                }
            }
            VerticalDivider(
                modifier = Modifier.height(24.dp),
                color = MaterialTheme.LaskColors.textSecondary,
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AuthorAvatar(
                modifier = Modifier.size(40.dp),
                char = authors.firstOrNull()?.firstOrNull()?.toString() ?: "?",
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = authors.firstOrNull() ?: stringResource(R.string.unknown_author),
                    style = MaterialTheme.LaskTypography.body2SemiBold,
                    color = MaterialTheme.LaskColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    autoSize = AuthorNameAutoSize,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = DateUtils.formatPublishDate(publishDate),
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    autoSize = MetaAutoSize,
                )
            }
        }
    }
}
