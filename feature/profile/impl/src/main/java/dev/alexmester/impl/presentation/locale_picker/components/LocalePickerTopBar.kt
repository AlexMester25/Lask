package dev.alexmester.impl.presentation.locale_picker.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import dev.alexmester.ui.components.buttons.LaskBackButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalePickerTopBar(
    modifier: Modifier = Modifier,
    title: String,
    isApplyEnabled: Boolean,
    onBack: () -> Unit,
    onApply: () -> Unit,
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
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            TextButton(
                onClick = onApply,
                enabled = isApplyEnabled,
            ) {
                Text(
                    text = "Apply",
                    style = MaterialTheme.LaskTypography.button1,
                    color = if (isApplyEnabled)
                        MaterialTheme.LaskColors.brand_blue
                    else
                        MaterialTheme.LaskColors.textSecondary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.LaskColors.backgroundPrimary,
        ),
    )
}