package dev.alexmester.impl.presentation.interests.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.interests.mvi.InterestsValidationError
import dev.alexmester.impl.presentation.interests.mvi.toUiString
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun InterestsErrorRow(
    modifier: Modifier = Modifier,
    validationError: InterestsValidationError?
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = validationError != null,
        enter = fadeIn(animationSpec = tween(200)) +
                expandVertically(
                    animationSpec = tween(200),
                    expandFrom = Alignment.Top,
                ),
        exit = fadeOut(animationSpec = tween(150)) +
                shrinkVertically(
                    animationSpec = tween(150),
                    shrinkTowards = Alignment.Top,
                ),
    ) {

        val lastError = remember { mutableStateOf(validationError) }
        LaunchedEffect(validationError) {
            if (validationError != null) {
                lastError.value = validationError
            }
        }

        lastError.value?.let { error ->
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.LaskColors.error,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = error.toUiString(),
                    style = MaterialTheme.LaskTypography.footnote,
                    color = MaterialTheme.LaskColors.error,
                )
            }
        }
    }
}