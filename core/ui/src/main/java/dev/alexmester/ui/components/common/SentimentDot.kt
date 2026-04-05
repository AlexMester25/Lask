package dev.alexmester.ui.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun SentimentDot(sentiment: Double) {
    val color = when {
        sentiment > 0.1 -> LaskPalette.Sentiment_Positive
        sentiment < -0.1 -> LaskPalette.Sentiment_Negative
        else -> LaskPalette.Sentiment_Neutral
    }
    Text(
        text = "●",
        color = color,
        style = MaterialTheme.LaskTypography.footnote,
    )
}