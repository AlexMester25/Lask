package dev.alexmester.impl.presentation.article_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.ui.design_system.LaskColors
import dev.alexmester.ui.design_system.LaskTypography

@Composable
internal fun CategoryFilterRow(
    categories: List<Pair<CategoryFilter, Int>>,
    selectedFilter: CategoryFilter,
    onFilterSelected: (CategoryFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = categories,
            key = { (filter, _) -> filter.key },
        ) { (filter, count) ->
            val isSelected = filter == selectedFilter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.LaskTypography.body1SemiBold,
                            color = MaterialTheme.LaskColors.textPrimary
                        )
                        Text(
                            text = "($count)",
                            style = MaterialTheme.LaskTypography.footnote,
                            color = MaterialTheme.LaskColors.textSecondary
                        )
                    }
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.LaskColors.brand_blue10,
                    containerColor = MaterialTheme.LaskColors.backgroundPrimary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.LaskColors.brand_blue10,
                    selectedBorderColor = MaterialTheme.LaskColors.brand_blue10,
                ),
            )
        }
    }
}

private val CategoryFilter.label: String
    get() = when (this) {
        CategoryFilter.All -> "All"
        CategoryFilter.Uncategorized -> "Other"
        is CategoryFilter.Named -> name.replaceFirstChar { it.uppercase() }
    }

/** Ключ для `LazyRow`: префиксы не дают категории «all» столкнуться с чипом «All». */
private val CategoryFilter.key: String
    get() = when (this) {
        CategoryFilter.All -> "filter:all"
        CategoryFilter.Uncategorized -> "filter:uncategorized"
        is CategoryFilter.Named -> "filter:$name"
    }
