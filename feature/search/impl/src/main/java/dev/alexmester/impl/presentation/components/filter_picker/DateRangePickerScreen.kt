package dev.alexmester.impl.presentation.components.filter_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.components.picker.LaskPickerApply
import dev.alexmester.ui.components.picker.LaskPickerTopBar
import dev.alexmester.ui.design_system.LaskColors
import dev.alexmester.ui.design_system.LaskTypography
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val MAX_DATE_RANGE_DAYS = 30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRangePickerScreen(
    earliestDate: String?,
    latestDate: String?,
    onApply: (earliest: String?, latest: String?) -> Unit,
    onBack: () -> Unit,
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val initialStart = remember(earliestDate) {
        earliestDate?.let {
            LocalDate.parse(it, formatter).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    }
    val initialEnd = remember(latestDate) {
        latestDate?.let {
            LocalDate.parse(it, formatter).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    }

    // Cap selection to the last [MAX_DATE_RANGE_DAYS] days (free plan limitation)
    val today = LocalDate.now(ZoneOffset.UTC)
    val minDate = today.minusDays(MAX_DATE_RANGE_DAYS.toLong())
        .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val maxDate = today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val pickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStart,
        initialSelectedEndDateMillis = initialEnd,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) =
                utcTimeMillis in minDate..maxDate
        }
    )

    fun Long?.toDateString() = this?.let {
        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().format(formatter)
    }

    val isApplyEnabled = pickerState.selectedStartDateMillis != null

    Scaffold(
        topBar = {
            LaskPickerTopBar(
                title = stringResource(R.string.search_date_period),
                onBack = onBack,
                apply = LaskPickerApply(
                    isEnabled = isApplyEnabled,
                    onClick = {
                        onApply(
                            pickerState.selectedStartDateMillis.toDateString(),
                            pickerState.selectedEndDateMillis.toDateString(),
                        )
                    },
                ),
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
        ) {
            DateRangePicker(
                state = pickerState,
                modifier = Modifier.weight(1f),
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.LaskColors.backgroundPrimary,
                    titleContentColor = MaterialTheme.LaskColors.textPrimary,
                    headlineContentColor = MaterialTheme.LaskColors.textPrimary,
                    weekdayContentColor = MaterialTheme.LaskColors.textSecondary,
                    dayContentColor = MaterialTheme.LaskColors.textPrimary,
                    selectedDayContainerColor = MaterialTheme.LaskColors.brand_blue,
                    selectedDayContentColor = MaterialTheme.LaskColors.backgroundPrimary,
                    dayInSelectionRangeContainerColor = MaterialTheme.LaskColors.brand_blue10,
                    dayInSelectionRangeContentColor = MaterialTheme.LaskColors.textPrimary,
                    todayContentColor = MaterialTheme.LaskColors.brand_blue,
                    todayDateBorderColor = MaterialTheme.LaskColors.brand_blue,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.search_date_range_hint, MAX_DATE_RANGE_DAYS),
                        style = MaterialTheme.LaskTypography.body2,
                        color = MaterialTheme.LaskColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                },
                headline = null,
                showModeToggle = false,
            )
        }
    }
}