package dev.alexmester.ui.components.bottom_bar

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Высота нижней навигационной панели вместе с системным inset'ом.
 * Экраны, у которых панель перекрывает низ списка, берут её для нижнего contentPadding.
 *
 * Хранится последнее измеренное значение и не сбрасывается, пока панель скрыта:
 * уходящий со вкладки экран остаётся на виду на время перехода, и его список не должен «прыгать».
 */
val LocalBottomBarHeight = compositionLocalOf<Dp> { 0.dp }