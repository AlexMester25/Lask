package dev.alexmester.ui.components.picker

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import dev.alexmester.ui.R
import dev.alexmester.ui.components.buttons.LaskBackButton
import dev.alexmester.ui.components.buttons.LaskTextButton
import dev.alexmester.ui.design_system.LaskColors
import dev.alexmester.ui.design_system.LaskTypography

/**
 * Опциональная кнопка подтверждения в [LaskPickerTopBar].
 * Если [label] не задан, используется общий ресурс `R.string.apply`.
 */
data class LaskPickerApply(
    val isEnabled: Boolean,
    val onClick: () -> Unit,
    val label: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaskPickerTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    apply: LaskPickerApply? = null,
    containerColor: Color? = null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            LaskBackButton(onClick = onBack)
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.LaskTypography.h5,
                color = MaterialTheme.LaskColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            if (apply != null) {
                LaskTextButton(
                    text = apply.label ?: stringResource(R.string.apply),
                    textColor = if (apply.isEnabled) MaterialTheme.LaskColors.brand_blue
                    else MaterialTheme.LaskColors.textSecondary,
                    onClick = apply.onClick,
                    isEnable = apply.isEnabled,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor ?: MaterialTheme.LaskColors.brand_blue10,
        ),
    )
}
