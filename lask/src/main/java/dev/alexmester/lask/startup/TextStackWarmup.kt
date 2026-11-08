package dev.alexmester.lask.startup

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import dev.alexmester.ui.desing_system.Inter
import dev.alexmester.ui.desing_system.Merriweather

/**
 * Первая раскладка текста с переносом строк в процессе стоит 23–33 мс: это одноразовая
 * инициализация текстового стека (Minikin/StaticLayout, разбиение на строки), а все
 * последующие вызовы укладываются в 0.1–0.7 мс. Без разогрева эта стоимость попадает
 * в первый `measure` ленты и растягивает кадр первого экрана.
 *
 * Разогрев идёт в фоне параллельно с инициализацией приложения и использует тот же путь,
 * что и UI (`TextMeasurer` с реальной типографикой), поэтому прогревается именно тот код,
 * который потом вызывает Compose. Проверено трассой: `TextLayout:initLayout` 22.8 мс
 * переезжает с main thread на рабочий поток, `TextStringSimpleNode::measure` первого
 * экрана падает с ~55 до ~25 мс.
 */
internal object TextStackWarmup {

    private const val TAG = "TextStackWarmup"

    fun warmUp(context: Context) {
        try {
            val measurer = TextMeasurer(
                defaultFontFamilyResolver = createFontFamilyResolver(context),
                defaultDensity = Density(context),
                defaultLayoutDirection = LayoutDirection.Ltr,
            )
            measure(measurer, TextStyle(fontFamily = Inter, fontSize = 18.sp, lineHeight = 24.sp))
            measure(measurer, TextStyle(fontFamily = Merriweather, fontSize = 16.sp, lineHeight = 30.sp))
        } catch (e: Exception) {
            // Разогрев — только оптимизация: его сбой не должен ронять старт приложения.
            Log.w(TAG, "Text warm-up skipped", e)
        }
    }

    private fun measure(measurer: TextMeasurer, style: TextStyle) {
        measurer.measure(
            text = AnnotatedString(WARM_UP_TEXT),
            style = style,
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            maxLines = 2,
            constraints = Constraints(maxWidth = WARM_UP_WIDTH_PX),
        )
    }

    private const val WARM_UP_TEXT =
        "Срочно: Европейский союз объявил о новых мерах — подробности и реакция рынков"

    /** Ширина карточки на экране 1080px, чтобы разогреть раскладку с переносом строк. */
    private const val WARM_UP_WIDTH_PX = 1080
}
