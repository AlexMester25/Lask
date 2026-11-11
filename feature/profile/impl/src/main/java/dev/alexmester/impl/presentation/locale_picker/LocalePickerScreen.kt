package dev.alexmester.impl.presentation.locale_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.locale_picker.components.CompatibilityWarningBanner
import dev.alexmester.impl.presentation.locale_picker.components.LocalePickerTopBar
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerIntent
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerSideEffect
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerState
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.ui.components.locale.LaskLocaleRowItem
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
    Scaffold(
        topBar = {
            LocalePickerTopBar(
                title = state.title.asString(),
                isApplyEnabled = state.isApplyEnabled,
                onBack = { onIntent(LocalePickerIntent.Back) },
                onApply = { onIntent(LocalePickerIntent.Apply) },
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.items, key = { it.code }) { item ->
                    LaskLocaleRowItem(
                        isSelected = item.code == state.selectedCode,
                        item = item,
                        onClick = { onIntent(LocalePickerIntent.SelectItem(item.code)) },
                    )
                }
            }
            CompatibilityWarningBanner(
                type = state.type,
                selectedCode = state.selectedCode,
                currentCode = state.otherLocaleCode,
                warning = state.compatibilityWarning,
                onAdaptSelf = { onIntent(LocalePickerIntent.ResolveCompatibility(adaptSelf = true)) },
                onAdaptOther = { onIntent(LocalePickerIntent.ResolveCompatibility(adaptSelf = false)) },
                onDismiss = { onIntent(LocalePickerIntent.DismissCompatibilityWarning) },
                modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
            )
        }
    }
}

