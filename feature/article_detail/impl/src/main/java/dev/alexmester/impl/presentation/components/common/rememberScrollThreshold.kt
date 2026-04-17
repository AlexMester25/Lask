package dev.alexmester.impl.presentation.components.common

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
internal fun rememberScrollThreshold(
    scrollState: ScrollState,
    threshold: Float = 0.5f,
    onThresholdReached: () -> Unit,
) {
    LaunchedEffect(scrollState) {
        snapshotFlow {
            val max = scrollState.maxValue
            if (max <= 0) return@snapshotFlow true
            scrollState.value.toFloat() / max >= threshold
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onThresholdReached() }
    }
}