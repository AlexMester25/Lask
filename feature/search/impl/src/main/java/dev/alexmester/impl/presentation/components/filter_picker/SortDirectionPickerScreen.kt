package dev.alexmester.impl.presentation.components.filter_picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.ui.R
import dev.alexmester.ui.components.picker.LaskPickerScreen

@Composable
internal fun SortDirectionPickerScreen(
    sortAscending: Boolean,
    onSelect: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val items = listOf(
        LocaleItem(
            code = "desc",
            displayName = stringResource(R.string.search_sort_newest),
            flag = "🔽",
        ),
        LocaleItem(
            code = "asc",
            displayName = stringResource(R.string.search_sort_oldest),
            flag = "🔼",
        ),
    )

    LaskPickerScreen(
        title = stringResource(R.string.search_sort),
        items = items,
        selectedCode = if (sortAscending) "asc" else "desc",
        onSelect = { code -> onSelect(code == "asc") },
        onBack = onBack,
    )
}
