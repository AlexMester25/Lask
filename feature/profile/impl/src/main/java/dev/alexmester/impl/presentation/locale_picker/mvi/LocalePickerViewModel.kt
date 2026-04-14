package dev.alexmester.impl.presentation.locale_picker.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.utils.BuildLocale
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocalePickerViewModel(
    private val type: LocalePickerType,
    private val preferencesDataSource: UserPreferencesDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(LocalePickerState(type = type))
    val state: StateFlow<LocalePickerState> = _state.asStateFlow()

    private val _sideEffects = Channel<LocalePickerSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        loadItems()
    }

    fun handleIntent(intent: LocalePickerIntent) {
        when (intent) {
            is LocalePickerIntent.SelectItem ->
                _state.update { it.copy(pendingCode = intent.code) }

            is LocalePickerIntent.Apply -> applySelection()

            is LocalePickerIntent.Back ->
                emitSideEffect(LocalePickerSideEffect.NavigateBack)
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            val prefs = preferencesDataSource.userPreferences.first()
            val currentCode = when (type) {
                LocalePickerType.COUNTRY  -> prefs.defaultCountry
                LocalePickerType.LANGUAGE -> prefs.defaultLanguage
            }

            val items = when (type) {
                LocalePickerType.COUNTRY  -> BuildLocale.buildCountryItems()
                LocalePickerType.LANGUAGE -> BuildLocale.buildLanguageItems()
            }

            _state.update {
                it.copy(
                    items = items,
                    selectedCode = currentCode,
                    pendingCode = currentCode,
                )
            }
        }
    }

    private fun applySelection() {
        val pending = _state.value.pendingCode
        if (pending.isEmpty()) return

        viewModelScope.launch {
            when (type) {
                LocalePickerType.COUNTRY ->
                    preferencesDataSource.updateLocaleManually(
                        country = pending,
                        language = preferencesDataSource.userPreferences.first().defaultLanguage,
                    )
                LocalePickerType.LANGUAGE ->
                    preferencesDataSource.updateLocaleManually(
                        country = preferencesDataSource.userPreferences.first().defaultCountry,
                        language = pending,
                    )
            }
            _state.update { it.copy(selectedCode = pending) }
            emitSideEffect(LocalePickerSideEffect.NavigateBack)
        }
    }

    private fun emitSideEffect(effect: LocalePickerSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}