package dev.alexmester.ui.components.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
internal fun LaskTopSnackbar(
    snackbarData: SnackbarData,
) {
    val isError = snackbarData.visuals is LaskSnackbarVisuals &&
            (snackbarData.visuals as LaskSnackbarVisuals).isError

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
        ) + fadeOut(),
    ) {
        Snackbar(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            containerColor = if (isError) {
                MaterialTheme.LaskColors.error
            } else {
                MaterialTheme.LaskColors.brand_blue10
            },
            contentColor = MaterialTheme.LaskColors.backgroundPrimary,
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = snackbarData.visuals.message,
                style = MaterialTheme.LaskTypography.body2,
                color = MaterialTheme.LaskColors.textPrimary
            )
        }
    }
}