package dev.alexmester.impl.presentation.settings.mvi

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.testing.MainDispatcherRule
import dev.alexmester.testing.fake.FakeUserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeUserPreferencesRepository()

    @Test
    fun `preferences are mapped into state`() = runTest {
        repository.emit(
            UserPreferences(
                defaultCountry = "de",
                defaultLanguage = "de",
                autoTranslateLanguage = "fr",
                isDarkTheme = true,
            )
        )

        val viewModel = SettingsViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(AppTheme.DARK, state.theme)
        assertEquals("de", state.countryCode)
        assertEquals("de", state.languageCode)
        assertEquals("fr", state.autoTranslateLanguage)
    }

    @Test
    fun `system theme clears the explicit dark flag`() = runTest {
        repository.emit(UserPreferences(isDarkTheme = true))
        val viewModel = SettingsViewModel(repository)
        advanceUntilIdle()

        viewModel.handleIntent(SettingsIntent.SetTheme(AppTheme.SYSTEM))
        advanceUntilIdle()

        assertNull(repository.preferences.isDarkTheme)
    }

    @Test
    fun `light theme is persisted`() = runTest {
        val viewModel = SettingsViewModel(repository)
        advanceUntilIdle()

        viewModel.handleIntent(SettingsIntent.SetTheme(AppTheme.LIGHT))
        advanceUntilIdle()

        assertFalse(repository.preferences.isDarkTheme!!)
    }

    @Test
    fun `locale navigation emits picker side effect`() = runTest {
        val viewModel = SettingsViewModel(repository)
        advanceUntilIdle()
        val effects = mutableListOf<SettingsSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.sideEffects.collect { effects += it }
        }

        viewModel.handleIntent(SettingsIntent.NavigateToCountry)
        advanceUntilIdle()

        assertEquals(
            listOf(SettingsSideEffect.NavigateToLocalePicker(LocalePickerType.COUNTRY)),
            effects,
        )
    }
}
