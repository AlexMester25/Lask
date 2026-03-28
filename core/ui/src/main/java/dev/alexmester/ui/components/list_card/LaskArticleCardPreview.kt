package dev.alexmester.ui.components.list_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

// ── Тестовые данные ───────────────────────────────────────────────────────────

private val articleWithImage = NewsArticleCardData(
    title = "Biden Signs Major Climate Bill Into Law After Senate Approval",
    imageUrl = "https://picsum.photos/seed/news1/200/200",
    category = "politics",
    publishDate = "2026-03-22",
    author = listOf("John Smith"),
    sentiment = 0.5,
)

private val articleNoImage = NewsArticleCardData(
    title = "Markets Crash as Global Economic Uncertainty Reaches New High",
    imageUrl = null,
    category = "business",
    publishDate = "2026-03-22",
    author = listOf("Jane Doe"),
    sentiment = -0.7,
)

private val articleNeutral = NewsArticleCardData(
    title = "Scientists Discover New Species in Amazon Rainforest",
    imageUrl = "https://picsum.photos/seed/news3/200/200",
    category = "science",
    publishDate = "2026-03-21",
    author = emptyList(),
    sentiment = 0.0,
)

private val articleLongTitle = NewsArticleCardData(
    title = "International Summit on Artificial Intelligence Regulation Concludes With Historic Agreement Between 50 Nations on Safety Standards",
    imageUrl = "https://picsum.photos/seed/news4/200/200",
    category = "technology",
    publishDate = "2026-03-20",
    author = listOf("Sarah Connor", "Jane Doe"),
    sentiment = 0.3,
)

private val articleNoMeta = NewsArticleCardData(
    title = "Quick Update: Server Maintenance Scheduled",
    imageUrl = null,
    category = null,
    publishDate = "2026-03-19",
    author = emptyList(),
    sentiment = null,
)


private data class NewsArticleCardData(
    val title: String,
    val imageUrl: String?,
    val category: String?,
    val publishDate: String,
    val author: List<String?>,
    val sentiment: Double?,
)

@Composable
private fun NewsArticleCardData.Composable(
    selectionMode: Boolean = false,
    isKept: Boolean = true,
    modifier: Modifier = Modifier,
) {
    LaskArticleCard(
        title = title,
        imageUrl = imageUrl,
        category = category,
        publishDate = publishDate,
        authors = author,
        sentiment = sentiment,
        selectionMode = selectionMode,
        isKept = isKept,
        modifier = modifier.fillMaxWidth(),
    )
}

// ── Секция-разделитель для превью ─────────────────────────────────────────────

@Composable
private fun Section(label: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.LaskTypography.footnoteSemiBold,
            color = MaterialTheme.LaskColors.textSecondary,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
        )
        content()
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Normal mode — Light", showBackground = true, widthDp = 390)
@Composable
private fun PreviewNormalLight() {
    LaskTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(vertical = 8.dp)
        ) {
            Section("С картинкой / позитивная тональность") {
                articleWithImage.Composable()
            }
            Section("Без картинки / негативная тональность") {
                articleNoImage.Composable()
            }
            Section("С картинкой / нейтральная / без автора") {
                articleNeutral.Composable()
            }
            Section("Длинный заголовок") {
                articleLongTitle.Composable()
            }
            Section("Нет категории, автора, тональности") {
                articleNoMeta.Composable()
            }
        }
    }
}

@Preview(name = "Normal mode — Dark", showBackground = true, backgroundColor = 0xFF0D0D0D, widthDp = 390)
@Composable
private fun PreviewNormalDark() {
    LaskTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(vertical = 8.dp)
        ) {
            Section("С картинкой / позитивная тональность") {
                articleWithImage.Composable()
            }
            Section("Без картинки / негативная тональность") {
                articleNoImage.Composable()
            }
            Section("С картинкой / нейтральная / без автора") {
                articleNeutral.Composable()
            }
            Section("Длинный заголовок") {
                articleLongTitle.Composable()
            }
            Section("Нет категории, автора, тональности") {
                articleNoMeta.Composable()
            }
        }
    }
}

@Preview(name = "Edit mode — all kept — Light", showBackground = true, widthDp = 390)
@Composable
private fun PreviewEditAllKeptLight() {
    LaskTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(vertical = 8.dp)
        ) {
            Section("Edit: все помечены как «сохранить»") {
                articleWithImage.Composable(selectionMode = true, isKept = true)
                articleNoImage.Composable(selectionMode = true, isKept = true)
                articleNeutral.Composable(selectionMode = true, isKept = true)
            }
        }
    }
}

/** Edit-режим — смешанное состояние */
@Preview(name = "Edit mode — mixed — Light", showBackground = true, widthDp = 390)
@Composable
private fun PreviewEditMixedLight() {
    LaskTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(vertical = 8.dp)
        ) {
            Section("Edit: жёлтая = оставить, серая = удалить") {
                articleWithImage.Composable(selectionMode = true, isKept = true)
                articleNoImage.Composable(selectionMode = true, isKept = false)
                articleNeutral.Composable(selectionMode = true, isKept = true)
            }
        }
    }
}
@Preview(name = "Edit mode — all kept — Dark", showBackground = true, widthDp = 390)
@Composable
private fun PreviewEditAllKeptDark() {
    LaskTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(vertical = 8.dp)
        ) {
            Section("Edit: все помечены как «сохранить»") {
                articleWithImage.Composable(selectionMode = true, isKept = true)
                articleNoImage.Composable(selectionMode = true, isKept = true)
                articleNeutral.Composable(selectionMode = true, isKept = true)
            }
        }
    }
}

@Preview(name = "Edit mode — mixed — Dark", showBackground = true, backgroundColor = 0xFF0D0D0D, widthDp = 390)
@Composable
private fun PreviewEditMixedDark() {
    LaskTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(vertical = 8.dp)
        ) {
            Section("Edit: жёлтая = оставить, серая = удалить") {
                articleWithImage.Composable(selectionMode = true, isKept = true)
                articleNoImage.Composable(selectionMode = true, isKept = false)
                articleNeutral.Composable(selectionMode = true, isKept = true)
            }
        }
    }
}