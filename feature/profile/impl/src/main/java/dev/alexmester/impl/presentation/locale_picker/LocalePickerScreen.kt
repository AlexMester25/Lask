package dev.alexmester.impl.presentation.locale_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.locale_picker.components.LocalePickerTopBar
import dev.alexmester.impl.presentation.locale_picker.components.LocaleRowItem
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerIntent
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerSideEffect
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerState
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.ui.components.buttons.LaskBackButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
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
                is LocalePickerSideEffect.NavigateBack -> onBack()
            }
        }
    }

    LocalePickerScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocalePickerScreenContent(
    state: LocalePickerState,
    onIntent: (LocalePickerIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            LocalePickerTopBar(
                modifier = Modifier,
                title = state.title,
                isApplyEnabled = state.isApplyEnabled,
                onBack = { onIntent(LocalePickerIntent.Back) },
                onApply = { onIntent(LocalePickerIntent.Apply) }
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = state.items,
                key = { it.code },
            ) { item ->
                val isSelected = item.code == state.pendingCode
                LocaleRowItem(
                    modifier = Modifier,
                    isSelected = isSelected,
                    item = item,
                    onClick = { onIntent(LocalePickerIntent.SelectItem(item.code)) }
                )
            }
        }
    }
}