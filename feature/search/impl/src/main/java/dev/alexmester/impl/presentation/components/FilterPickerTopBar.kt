package dev.alexmester.impl.presentation.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import dev.alexmester.ui.R
import dev.alexmester.ui.components.buttons.LaskBackButton
import dev.alexmester.ui.components.buttons.LaskTextButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterPickerTopBar(
    title: String,
    isApplyEnabled: Boolean,
    onBack: () -> Unit,
    onApply: () -> Unit,
    showApply: Boolean = true,
) {
    CenterAlignedTopAppBar(
        navigationIcon = { LaskBackButton(onClick = onBack) },
        title = {
            Text(
                text = title,
                style = MaterialTheme.LaskTypography.h5,
                color = MaterialTheme.LaskColors.textPrimary,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            if (showApply) {
                LaskTextButton(
                    text = stringResource(R.string.apply),
                    textColor = if (isApplyEnabled) MaterialTheme.LaskColors.brand_blue
                    else MaterialTheme.LaskColors.textSecondary,
                    onClick = onApply,
                    isEnable = isApplyEnabled,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.LaskColors.brand_blue10,
        ),
    )
}