package dev.alexmester.impl.presentation.system.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.alexmester.impl.presentation.system.mvi.SystemIntent
import dev.alexmester.ui.components.buttons.LaskBackButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SystemTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit
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
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.LaskColors.brand_blue10,
        ),
    )
}