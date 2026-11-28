package dev.alexmester.impl.presentation.locale_picker.mvi

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.domain.locale.CompatibilityWarning
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalePickerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeUserPreferencesRepository()

    @Test
    fun `selection is not persisted before apply`() = runTest {
        val viewModel = viewModel(LocalePickerType.LANGUAGE)
        advanceUntilIdle()

        viewModel.handleIntent(LocalePickerIntent.SelectItem("de"))
        advanceUntilIdle()

        assertEquals("de", viewModel.state.value.selectedCode)
        assertEquals("en", repository.preferences.defaultLanguage)

        viewModel.handleIntent(LocalePickerIntent.Apply)
        advanceUntilIdle()

        assertEquals("de", repository.preferences.defaultLanguage)
        assertEquals("us", repository.preferences.defaultCountry)
    }

    @Test
    fun `incompatible language selection exposes a warning`() = runTest {
        val viewModel = viewModel(LocalePickerType.LANGUAGE)
        advanceUntilIdle()

        viewModel.handleIntent(LocalePickerIntent.SelectItem("de"))
        advanceUntilIdle()

        assertEquals(
            CompatibilityWarning(suggestedCountry = "de", suggestedLanguage = "en"),
            viewModel.state.value.compatibilityWarning,
        )
    }

    @Test
    fun `apply is disabled while selection matches the current locale`() = runTest {
        val viewModel = viewModel(LocalePickerType.COUNTRY)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isApplyEnabled)

        viewModel.handleIntent(LocalePickerIntent.SelectItem("de"))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isApplyEnabled)
    }

    @Test
    fun `auto translate selection is applied to the translate language`() = runTest {
        val viewModel = viewModel(LocalePickerType.AUTO_TRANSLATE_LANGUAGE)
        advanceUntilIdle()

        viewModel.handleIntent(LocalePickerIntent.SelectItem("de"))
        advanceUntilIdle()

        assertNull(viewModel.state.value.compatibilityWarning)

        viewModel.handleIntent(LocalePickerIntent.Apply)
        advanceUntilIdle()

        assertEquals("de", repository.preferences.autoTranslateLanguage)
        assertEquals("en", repository.preferences.defaultLanguage)
    }

    @Test
    fun `apply navigates back`() = runTest {
        val viewModel = viewModel(LocalePickerType.LANGUAGE)
        advanceUntilIdle()
        val effects = mutableListOf<LocalePickerSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.sideEffects.collect { effects += it }
        }

        viewModel.handleIntent(LocalePickerIntent.SelectItem("de"))
        advanceUntilIdle()
        viewModel.handleIntent(LocalePickerIntent.Apply)
        advanceUntilIdle()

        assertEquals(listOf(LocalePickerSideEffect.NavigateBack), effects)
    }

    private fun viewModel(type: LocalePickerType) = LocalePickerViewModel(
        type = type,
        userPreferencesRepository = repository,
    )
}
