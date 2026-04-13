package dev.alexmester.impl.presentation.system.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.system.mvi.AppTheme
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
internal fun ThemeSelector(
    selected: AppTheme,
    onSelect: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppTheme.entries.forEach { theme ->
            ThemeChip(
                label = theme.label,
                isSelected = theme == selected,
                onClick = { onSelect(theme) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50.dp)
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.LaskColors.brand_blue
            else MaterialTheme.LaskColors.backgroundPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.LaskColors.brand_blue
            else MaterialTheme.LaskColors.brand_blue10
        ),
    ) {
        Text(
            modifier = Modifier,
            text = label,
            style = MaterialTheme.LaskTypography.footnote,
            color = if (isSelected) Color.White else MaterialTheme.LaskColors.textPrimary
        )
    }
}

private val AppTheme.label: String
    get() = when (this) {
        AppTheme.SYSTEM -> "System"
        AppTheme.LIGHT  -> "Light"
        AppTheme.DARK   -> "Dark"
    }