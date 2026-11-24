package dev.alexmester.ui.components.scroll_to_top

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Состояние кнопки «наверх» для ленивого списка.
 *
 * Возврат выполняется мгновенно через [LazyListState.scrollToItem], а не через
 * `animateScrollToItem`: последний прокручивает viewport через каждый промежуточный
 * элемент, поэтому на длинном списке это долго и дорого. Вместо этого контент
 * списка коротко гасится и проявляется — см. [contentAlpha].
 */
@Stable
class ScrollToTopState internal constructor(
    private val listState: LazyListState,
    private val scope: CoroutineScope,
    private val threshold: Int,
) {
    private val alpha = Animatable(1f)

    /**
     * Прозрачность контента списка. Читать внутри `Modifier.graphicsLayer { }`,
     * чтобы значение потреблялось в фазе draw и не вызывало рекомпозицию.
     */
    val contentAlpha: Float
        get() = alpha.value

    internal val isVisible: Boolean
        get() = listState.firstVisibleItemIndex > threshold

    fun scrollToTop() {
        scope.launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = ScrollToTopDefaults.FadeOutMs,
                    easing = ScrollToTopDefaults.Easing,
                ),
            )
            listState.scrollToItem(0)
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ScrollToTopDefaults.FadeInMs,
                    easing = ScrollToTopDefaults.Easing,
                ),
            )
        }
    }
}

@Composable
fun rememberScrollToTopState(
    listState: LazyListState,
    threshold: Int = ScrollToTopDefaults.Threshold,
): ScrollToTopState {
    val scope = rememberCoroutineScope()
    return remember(listState, scope, threshold) {
        ScrollToTopState(
            listState = listState,
            scope = scope,
            threshold = threshold,
        )
    }
}

object ScrollToTopDefaults {
    /** С какого видимого элемента показывать кнопку. */
    const val Threshold = 4

    const val FadeOutMs = 100
    const val FadeInMs = 200
    const val FabEnterMs = 150
    const val FabExitMs = 100
    const val InitialScale = 0.8f

    /** M3 Emphasized. */
    val Easing: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}
