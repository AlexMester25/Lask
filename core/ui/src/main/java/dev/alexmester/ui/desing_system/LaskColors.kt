package dev.alexmester.ui.desing_system


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class LaskColors(

    // ── Text ──────────────────────────────────
    val textPrimary: Color,
    val textSecondary: Color,
    val textLink: Color,

    // ── Brand ─────────────────────────────────
    val brand_blue: Color,
    val brand_blue10: Color,

    // ── Background ────────────────────────────
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,

    // ── System ────────────────────────────────
    val success: Color,
    val error: Color,
    val warning: Color,
    val informative: Color,

    // ── Meta ──────────────────────────────────
    val isDark: Boolean,
)

val LaskLightColors = LaskColors(
    textPrimary = LaskPalette.TextPrimaryLight,
    textSecondary = LaskPalette.TextSecondaryLight,
    textLink = LaskPalette.TextLinkLight,
    brand_blue = LaskPalette.Brand_Blue,
    brand_blue10 = LaskPalette.Brand_BlueLight10,
    backgroundPrimary = LaskPalette.BackgroundPrimaryLight,
    backgroundSecondary = LaskPalette.BackgroundSecondaryLight,
    success = LaskPalette.Success,
    error = LaskPalette.Error,
    warning = LaskPalette.Warning,
    informative = LaskPalette.Informative,
    isDark = false,
)

val LaskDarkColors = LaskColors(
    textPrimary = LaskPalette.TextPrimaryDark,
    textSecondary = LaskPalette.TextSecondaryDark,
    textLink = LaskPalette.TextLinkDark,
    brand_blue = LaskPalette.Brand_Blue,
    brand_blue10 = LaskPalette.Brand_BlueDark10,
    backgroundPrimary = LaskPalette.BackgroundPrimaryDark,
    backgroundSecondary = LaskPalette.BackgroundSecondaryDark,
    success = LaskPalette.Success,
    error = LaskPalette.Error,
    warning = LaskPalette.Warning,
    informative = LaskPalette.Informative,
    isDark = true,
)

val LocalLaskColors = staticCompositionLocalOf { LaskLightColors }

val MaterialTheme.LaskColors: LaskColors
    @Composable
    @ReadOnlyComposable
    get() = LocalLaskColors.current