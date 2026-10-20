package dev.alexmester.lask.theme_switch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val state: StateFlow<ThemeState> =
        userPreferencesRepository.observeUserPreferences()
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