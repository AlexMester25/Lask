package dev.alexmester.ui.components.pagination

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

/** За сколько элементов до конца начинать догрузку следующей страницы. */
private const val LOAD_MORE_THRESHOLD = 4

/**
 * Догружает следующую страницу, когда пользователь подходит к концу списка.
 *
 * Побочный эффект намеренно вынесен в отдельный composable: триггер одинаковый
 * для всех списков с растущим окном, а его отсутствие в одном из них — тихая
 * ошибка (список просто перестаёт догружаться).
 *
 * @param itemCount размер текущего списка. Нужен в ключе [LaunchedEffect]: если
 * после загрузки страницы экран всё ещё заполнен (планшет, большой экран),
 * окно должно продолжить расти без дополнительного скролла.
 */
@Composable
fun LaskLoadMoreEffect(
    listState: LazyListState,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    itemCount: Int,
    onLoadMore: () -> Unit,
) {
    val shouldLoadMore by remember(canLoadMore, isLoadingMore, listState) {
        derivedStateOf {
            if (!canLoadMore || isLoadingMore) return@derivedStateOf false
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf false
            lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore, itemCount) {
        if (shouldLoadMore) onLoadMore()
    }
}
