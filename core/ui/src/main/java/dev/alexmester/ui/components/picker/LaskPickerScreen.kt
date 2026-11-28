package dev.alexmester.ui.components.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.ui.components.locale.LaskLocaleRowItem
import dev.alexmester.ui.design_system.LaskColors

/**
 * Универсальный stateless-экран выбора элемента из списка [LocaleItem].
 *
 * Не владеет выбранным значением: вызывающий экран/ViewModel решает,
 * применяется выбор сразу ([apply] == null) или по кнопке подтверждения.
 *
 * @param clearItem опциональный первый пункт «Any/All», снимающий выбор.
 * @param topBarContainerColor цвет top bar'а; по умолчанию `brand_blue10`.
 * @param overlay слот поверх списка (например, предупреждение о совместимости).
 */
@Composable
fun LaskPickerScreen(
    title: String,
    items: List<LocaleItem>,
    selectedCode: String?,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    clearItem: LocaleItem? = null,
    onClear: (() -> Unit)? = null,
    apply: LaskPickerApply? = null,
    topBarContainerColor: Color? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LaskPickerTopBar(
                title = title,
                onBack = onBack,
                apply = apply,
                containerColor = topBarContainerColor,
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                if (clearItem != null && onClear != null) {
                    item(key = "clear") {
                        LaskLocaleRowItem(
                            isSelected = selectedCode == null,
                            item = clearItem,
                            onClick = onClear,
                        )
                    }
                }
                items(items = items, key = { it.code }) { item ->
                    LaskLocaleRowItem(
                        isSelected = item.code == selectedCode,
                        item = item,
                        onClick = { onSelect(item.code) },
                    )
                }
            }
            overlay?.invoke(this)
        }
    }
}
