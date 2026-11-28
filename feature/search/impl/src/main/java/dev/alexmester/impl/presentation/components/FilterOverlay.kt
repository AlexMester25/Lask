package dev.alexmester.impl.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import dev.alexmester.impl.domain.model.FilterType
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.impl.presentation.components.filter_picker.DateRangePickerScreen
import dev.alexmester.impl.presentation.components.filter_picker.SortDirectionPickerScreen
import dev.alexmester.models.categories.SearchCategory
import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.ui.R
import dev.alexmester.ui.components.picker.LaskPickerScreen
import dev.alexmester.utils.locale.LocaleUtils

@Composable
fun FilterOverlay(
    filterType: FilterType,
    filters: SearchFilters,
    onFiltersChanged: (SearchFilters) -> Unit,
    onBack: () -> Unit,
) {
    when (filterType) {
        FilterType.CATEGORY -> {
            val items = remember {
                SearchCategory.entries.map { category ->
                    LocaleItem(
                        code = category.code,
                        displayName = category.displayName,
                        flag = category.emoji,
                    )
                }
            }
            LaskPickerScreen(
                title = stringResource(R.string.search_category),
                items = items,
                selectedCode = filters.category,
                onSelect = { code -> onFiltersChanged(filters.copy(category = code)) },
                onBack = onBack,
                clearItem = LocaleItem(
                    code = "",
                    displayName = stringResource(R.string.search_all_categories),
                    flag = "🌐",
                ),
                onClear = { onFiltersChanged(filters.copy(category = null)) },
            )
        }

        FilterType.COUNTRY -> {
            val items = remember { LocaleUtils.buildCountryItems() }
            LaskPickerScreen(
                title = stringResource(R.string.search_country),
                items = items,
                selectedCode = filters.country,
                onSelect = { code -> onFiltersChanged(filters.copy(country = code)) },
                onBack = onBack,
                clearItem = LocaleItem(
                    code = "",
                    displayName = stringResource(R.string.search_any_country),
                    flag = "🌐",
                ),
                onClear = { onFiltersChanged(filters.copy(country = null)) },
            )
        }

        FilterType.LANGUAGE -> {
            val items = remember { LocaleUtils.buildLanguageItems() }
            LaskPickerScreen(
                title = stringResource(R.string.search_language),
                items = items,
                selectedCode = filters.language,
                onSelect = { code -> onFiltersChanged(filters.copy(language = code)) },
                onBack = onBack,
                clearItem = LocaleItem(
                    code = "",
                    displayName = stringResource(R.string.search_any_language),
                    flag = "🌐",
                ),
                onClear = { onFiltersChanged(filters.copy(language = null)) },
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
                onSelect = { asc -> onFiltersChanged(filters.copy(sortAscending = asc)) },
                onBack = onBack,
            )
        }
    }
}
