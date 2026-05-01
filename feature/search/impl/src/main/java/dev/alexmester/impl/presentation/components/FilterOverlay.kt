package dev.alexmester.impl.presentation.components

import androidx.compose.runtime.Composable
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.impl.presentation.components.filter_picker.CategoryPickerScreen
import dev.alexmester.impl.presentation.components.filter_picker.CountryPickerScreen
import dev.alexmester.impl.presentation.components.filter_picker.DateRangePickerScreen
import dev.alexmester.impl.presentation.components.filter_picker.LanguagePickerScreen
import dev.alexmester.impl.presentation.components.filter_picker.SortDirectionPickerScreen
import dev.alexmester.impl.domain.model.FilterType

@Composable
fun FilterOverlay(
    filterType: FilterType,
    filters: SearchFilters,
    onFiltersChanged: (SearchFilters) -> Unit,
    onBack: () -> Unit,
) {
    when (filterType) {
        FilterType.CATEGORY -> {
            CategoryPickerScreen(
                selectedCategory = filters.category,
                onSelect = { cat -> onFiltersChanged(filters.copy(category = cat)) },
                onBack = onBack,
            )
        }
        FilterType.COUNTRY -> {
            CountryPickerScreen(
                selectedCountry = filters.country,
                onSelect = { code -> onFiltersChanged(filters.copy(country = code)) },
                onBack = onBack,
            )
        }
        FilterType.LANGUAGE -> {
            LanguagePickerScreen(
                selectedLanguage = filters.language,
                onSelect = { code -> onFiltersChanged(filters.copy(language = code)) },
                onBack = onBack,
            )
        }
        FilterType.DATE -> {
            DateRangePickerScreen(
                earliestDate = filters.earliestDate,
                latestDate = filters.latestDate,
                onApply = { earliest, latest ->
                    onFiltersChanged(filters.copy(earliestDate = earliest, latestDate = latest))
                },
                onBack = onBack,
            )
        }
        FilterType.SORT -> {
            SortDirectionPickerScreen(
                sortAscending = filters.sortAscending,
                onSelect = { asc ->
                    onFiltersChanged(filters.copy(sortAscending = asc))
                    onBack()
                },
                onBack = onBack,
            )
        }
    }
}