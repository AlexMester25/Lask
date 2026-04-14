package dev.alexmester.lask.theme_switch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(
    private val preferencesDataSource: UserPreferencesDataSource,
) : ViewModel() {

    val state: StateFlow<ThemeState> =
        preferencesDataSource.userPreferences
            .map { prefs ->
                ThemeState(
                    isDarkTheme = prefs.isDarkTheme
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeState(null)
            )
}