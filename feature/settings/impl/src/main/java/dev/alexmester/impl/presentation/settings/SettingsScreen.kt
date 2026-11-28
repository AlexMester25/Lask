package dev.alexmester.impl.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.settings.components.SettingsValueRow
import dev.alexmester.impl.presentation.settings.components.ThemeSelector
import dev.alexmester.impl.presentation.settings.mvi.SettingsIntent
import dev.alexmester.impl.presentation.settings.mvi.SettingsSideEffect
import dev.alexmester.impl.presentation.settings.mvi.SettingsState
import dev.alexmester.impl.presentation.settings.mvi.SettingsViewModel
import dev.alexmester.ui.R
import dev.alexmester.ui.components.picker.LaskPickerTopBar
import dev.alexmester.ui.design_system.LaskColors
import dev.alexmester.ui.design_system.LaskTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToLocalePicker: (LocalePickerType) -> Unit,
    onNavigateToAutoTranslatePicker: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is SettingsSideEffect.NavigateBack -> onBack()
                is SettingsSideEffect.NavigateToLocalePicker ->
                    onNavigateToLocalePicker(effect.type)
                is SettingsSideEffect.NavigateToAutoTranslatePicker ->
                    onNavigateToAutoTranslatePicker()
            }
        }
    }

    SettingsScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
internal fun SettingsScreenContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            LaskPickerTopBar(
                title = stringResource(R.string.profile_menu_system),
                onBack = { onIntent(SettingsIntent.Back) },
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.system_theme),
                    style = MaterialTheme.LaskTypography.body1,
                    color = MaterialTheme.LaskColors.textPrimary,
                    modifier = Modifier,
                )
                ThemeSelector(
                    selected = state.theme,
                    onSelect = { onIntent(SettingsIntent.SetTheme(it)) },
                    modifier = Modifier.weight(1f),
                )
            }
            SettingsValueRow(
                label = stringResource(R.string.system_locale_language),
                value = state.languageDisplayName,
                onClick = { onIntent(SettingsIntent.NavigateToLanguage) },
            )
            SettingsValueRow(
                label = stringResource(R.string.system_locale_country),
                value = state.countryDisplayName,
                onClick = { onIntent(SettingsIntent.NavigateToCountry) },
            )
            SettingsValueRow(
                label = stringResource(R.string.system_locale_translate),
                value = state.autoTranslateDisplayName,
                onClick = { onIntent(SettingsIntent.NavigateToAutoTranslateLanguage) },
            )
        }
    }
}
