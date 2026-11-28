package dev.alexmester.impl.presentation.locale_picker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.locale_picker.components.CompatibilityWarningBanner
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerIntent
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerSideEffect
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerState
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.ui.components.picker.LaskPickerApply
import dev.alexmester.ui.components.picker.LaskPickerScreen
import dev.alexmester.ui.design_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LocalePickerScreen(
    type: LocalePickerType,
    onBack: () -> Unit,
    viewModel: LocalePickerViewModel = koinViewModel(
        parameters = { parametersOf(type) }
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                LocalePickerSideEffect.NavigateBack -> onBack()
            }
        }
    }

    LocalePickerScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
internal fun LocalePickerScreenContent(
    state: LocalePickerState,
    onIntent: (LocalePickerIntent) -> Unit,
) {
    LaskPickerScreen(
        title = state.title.asString(),
        items = state.items,
        selectedCode = state.selectedCode,
        onSelect = { onIntent(LocalePickerIntent.SelectItem(it)) },
        onBack = { onIntent(LocalePickerIntent.Back) },
        apply = LaskPickerApply(
            isEnabled = state.isApplyEnabled,
            onClick = { onIntent(LocalePickerIntent.Apply) },
        ),
        topBarContainerColor = MaterialTheme.LaskColors.backgroundPrimary,
        overlay = {
            CompatibilityWarningBanner(
                type = state.type,
                selectedCode = state.selectedCode,
                currentCode = state.otherLocaleCode,
                warning = state.compatibilityWarning,
                onAdaptSelf = { onIntent(LocalePickerIntent.ResolveCompatibility(adaptSelf = true)) },
                onAdaptOther = { onIntent(LocalePickerIntent.ResolveCompatibility(adaptSelf = false)) },
                onDismiss = { onIntent(LocalePickerIntent.DismissCompatibilityWarning) },
                modifier = Modifier.align(Alignment.TopCenter).zIndex(1f),
            )
        },
    )
}
