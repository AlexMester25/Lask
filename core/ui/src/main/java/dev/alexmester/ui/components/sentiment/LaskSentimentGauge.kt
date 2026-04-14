package dev.alexmester.ui.components.sentiment

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTypography
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun LaskSentimentGauge(
    sentiment: Double,
    modifier: Modifier = Modifier,
    arcStrokeWidth: Dp = 16.dp,
) {
    val normalized = ((sentiment + 1.0) / 2.0).coerceIn(0.0, 1.0).toFloat()
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(normalized) {
        animatedValue.animateTo(
            targetValue = normalized,
            animationSpec = tween(durationMillis = 800),
        )
    }

    val needleAngleDeg = 180f + animatedValue.value * 180f
    val needleColor = MaterialTheme.LaskColors.textSecondary

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
        ) {
            val strokePx = arcStrokeWidth.toPx().coerceAtLeast(12.dp.toPx())
            val padding = strokePx / 2f
            val arcSize = Size(
                width = size.width - strokePx,
                height = size.width - strokePx,
            )
            val topLeft = Offset(padding, padding)
            val centerX = size.width / 2f
            val centerY = size.width / 2f

            // ── Фоновая дуга ──────────────────────────────────────────────
            drawArc(
                color = Color.Gray.copy(alpha = 0.15f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt),
            )

            drawArc(
                color = LaskPalette.Sentiment_Negative,
                startAngle = 180f,
                sweepAngle = 61f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt),
            )
            drawArc(
                color = LaskPalette.Sentiment_Neutral,
                startAngle = 240f,
                sweepAngle = 61f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt),
            )
            drawArc(
                color = LaskPalette.Sentiment_Positive,
                startAngle = 300f,
                sweepAngle = 60f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt),
            )

            drawArc(
                color = LaskPalette.Sentiment_Negative,
                startAngle = 180f,
                sweepAngle = 2f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
            drawArc(
                color = LaskPalette.Sentiment_Positive,
                startAngle = 358f,
                sweepAngle = 2f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            val needleRadius = centerY - strokePx * 1.6f
            drawNeedle(
                centerX = centerX,
                centerY = centerY,
                radius = needleRadius,
                angleDeg = needleAngleDeg,
                color = needleColor,
                strokePx = strokePx * 0.5f,
            )
        }
    }
}

@Composable
fun SentimentText(
    modifier: Modifier = Modifier,
    sentiment: Double
) {
    val normalized = ((sentiment + 1.0) / 2.0).coerceIn(0.0, 1.0).toFloat()
    val score = (normalized * 100).toInt()
    val labelText = "$score"

    Text(
        text = labelText,
        style = MaterialTheme.LaskTypography.footnoteSemiBold,
        color = sentimentColor(sentiment),
        modifier = modifier,
    )
}


private fun DrawScope.drawNeedle(
    centerX: Float,
    centerY: Float,
    radius: Float,
    angleDeg: Float,
    color: Color,
    strokePx: Float,
) {
    val angleRad = Math.toRadians(angleDeg.toDouble())
    val tipX = centerX + radius * cos(angleRad).toFloat()
    val tipY = centerY + radius * sin(angleRad).toFloat()

    drawCircle(
        color = color,
        radius = strokePx * 0.8f,
        center = Offset(centerX, centerY),
    )
    drawLine(
        color = color,
        start = Offset(centerX, centerY),
        end = Offset(tipX, tipY),
        strokeWidth = strokePx * 0.6f,
        cap = StrokeCap.Round,
    )
}

@Composable
private fun sentimentLabel(sentiment: Double): String = when {
    sentiment > 0.1 -> stringResource(R.string.sentiment_positive)
    sentiment < -0.1 -> stringResource(R.string.sentiment_negative)
    else -> stringResource(R.string.sentiment_neutral)
}

private fun sentimentColor(sentiment: Double): Color = when {
    sentiment > 0.1 -> LaskPalette.Sentiment_Positive
    sentiment < -0.1 -> LaskPalette.Sentiment_Negative
    else -> LaskPalette.Sentiment_Neutral
}