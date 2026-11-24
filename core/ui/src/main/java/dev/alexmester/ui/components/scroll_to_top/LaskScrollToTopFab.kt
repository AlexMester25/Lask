package dev.alexmester.ui.components.scroll_to_top

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.alexmester.ui.R
import dev.alexmester.ui.design_system.LaskColors

/**
 * Кнопка «наверх» для ленивого списка.
 *
 * Показывается, когда первый видимый элемент ушёл дальше [ScrollToTopDefaults.Threshold].
 * Позиционирование (например, отступ от нижней навигации) задаётся через [modifier].
 *
 * @param visible дополнительное условие показа, например `!isEditMode`.
 */
@Composable
fun LaskScrollToTopFab(
    state: ScrollToTopState,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    contentDescription: String = stringResource(R.string.scroll_to_top),
    containerColor: Color = MaterialTheme.LaskColors.brand_blue,
    contentColor: Color = Color.White,
) {
    val isScrolledDown by remember(state) {
        derivedStateOf { state.isVisible }
    }

    AnimatedVisibility(
        visible = visible && isScrolledDown,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = ScrollToTopDefaults.FabEnterMs,
                easing = ScrollToTopDefaults.Easing,
            ),
        ) + scaleIn(
            initialScale = ScrollToTopDefaults.InitialScale,
            animationSpec = tween(
                durationMillis = ScrollToTopDefaults.FabEnterMs,
                easing = ScrollToTopDefaults.Easing,
            ),
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = ScrollToTopDefaults.FabExitMs),
        ) + scaleOut(
            targetScale = ScrollToTopDefaults.InitialScale,
            animationSpec = tween(durationMillis = ScrollToTopDefaults.FabExitMs),
        ),
    ) {
        FloatingActionButton(
            onClick = state::scrollToTop,
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = contentDescription,
            )
        }
    }
}
