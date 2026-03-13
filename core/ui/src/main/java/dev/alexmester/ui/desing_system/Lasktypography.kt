package dev.alexmester.ui.desing_system

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.alexmester.ui.R

val Inter = FontFamily(
    Font(R.font.inter_regular,  FontWeight.Normal),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold,     FontWeight.Bold),
)

val Merriweather = FontFamily(
    Font(R.font.merriweather_regular,  FontWeight.Normal),
    Font(R.font.merriweather_semibold, FontWeight.SemiBold),
)

@Immutable
data class LaskTypography(

    // ── Headings ──────────────────────────────
    val h1: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,      // 700
        fontSize   = 48.sp,
    ),
    val h2: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,      // 700
        fontSize   = 40.sp,
    ),
    val h3: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 32.sp,
    ),
    val h4: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 24.sp,
    ),
    val h5: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 18.sp,
    ),

    // ── Body ──────────────────────────────────
    val body1SemiBold: TextStyle = TextStyle(
        fontFamily = Merriweather,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 16.sp,
    ),
    val body1: TextStyle = TextStyle(
        fontFamily = Merriweather,
        fontWeight = FontWeight.Normal,    // 400
        fontSize   = 16.sp,
    ),
    val body2SemiBold: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 14.sp,
    ),
    val body2: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,    // 400
        fontSize   = 14.sp,
    ),

    // ── Buttons ───────────────────────────────
    val button1: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 16.sp,
    ),
    val button2: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 14.sp,
    ),

    // ── Footnote ──────────────────────────────
    val footnoteSemiBold: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,  // 600
        fontSize   = 12.sp,
    ),
    val footnote: TextStyle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,    // 400
        fontSize   = 12.sp,
    ),
)

val LocalLaskTypography = staticCompositionLocalOf { LaskTypography() }

val MaterialTheme.LaskTypography: LaskTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalLaskTypography.current