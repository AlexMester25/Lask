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
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

// ── Тестовые данные ───────────────────────────────────────────────────────────

private val articleWithImage = NewsArticle(
    id = 1L,
    title = "Biden Signs Major Climate Bill Into Law After Senate Approval",
    text = null,
    summary = null,
    url = "https://example.com/1",
    image = "https://picsum.photos/seed/news1/200/200",
    video = null,
    publishDate = "2026-03-22 10:00:00",
    authors = listOf("John Smith"),
    category = "politics",
    language = "en",
    sourceCountry = "us",
    sentiment = 0.5,
)

private val articleNoImage = NewsArticle(
    id = 2L,
    title = "Markets Crash as Global Economic Uncertainty Reaches New High",
    text = null,
    summary = null,
    url = "https://example.com/2",
    image = null,
    video = null,
    publishDate = "2026-03-22 12:00:00",
    authors = listOf("Jane Doe"),
    category = "business",
    language = "en",
    sourceCountry = "ru",
    sentiment = -0.7,
)

private val articleNeutral = NewsArticle(
    id = 3L,
    title = "Scientists Discover New Species in Amazon Rainforest",
    text = null,
    summary = null,
    url = "https://example.com/3",
    image = "https://picsum.photos/seed/news3/200/200",
    video = null,
    publishDate = "2026-03-21 09:00:00",
    authors = emptyList(),
    category = "science",
    language = "en",
    sourceCountry = "us",
    sentiment = 0.0,
)

private val articleLongTitle = NewsArticle(
    id = 4L,
    title = "International Summit on Artificial Intelligence Regulation Concludes With Historic Agreement Between 50 Nations on Safety Standards",
    text = null,
    summary = null,
    url = "https://example.com/4",
    image = "https://picsum.photos/seed/news4/200/200",
    video = null,
    publishDate = "2026-03-20 15:30:00",
    authors = listOf("Sarah Connor", "Jane Doe"),
    category = "technology",
    language = "en",
    sourceCountry = "us",
    sentiment = 0.3,
)

private val articleNoMeta = NewsArticle(
    id = 5L,
    title = "Quick Update: Server Maintenance Scheduled",
    text = null,
    summary = null,
    url = "https://example.com/5",
    image = null,
    video = null,
    publishDate = "2026-03-19 08:00:00",
    authors = emptyList(),
    category = null,
    language = null,
    sourceCountry = null,
    sentiment = null,
)

// ── Хелпер ────────────────────────────────────────────────────────────────────

@Composable
private fun Section(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.LaskTypography.footnoteSemiBold,
            color = MaterialTheme.LaskColors.textSecondary,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
        )
        content()
    }
}

@Composable
private fun PreviewCard(
    article: NewsArticle,
    selectionMode: Boolean = false,
    isKept: Boolean = true,
    isRead: Boolean = false,
) {
    LaskArticleCard(
        modifier = Modifier.fillMaxWidth(),
        article = article,
        selectionMode = selectionMode,
        isKept = isKept,
        isRead = isRead,
    )
}

// ── Previews — Normal mode ────────────────────────────────────────────────────

@Preview(name = "Normal — Light", showBackground = true, widthDp = 390)
@Composable
private fun PreviewNormalLight() {
    LaskTheme(darkTheme = false) {
        Column(modifier = Modifier.background(MaterialTheme.LaskColors.backgroundPrimary).padding(vertical = 8.dp)) {
            Section("С картинкой / позитивная тональность") { PreviewCard(articleWithImage) }
            Section("Без картинки / негативная тональность") { PreviewCard(articleNoImage) }
            Section("С картинкой / нейтральная / без автора") { PreviewCard(articleNeutral) }
            Section("Длинный заголовок") { PreviewCard(articleLongTitle) }
            Section("Нет категории, автора, тональности") { PreviewCard(articleNoMeta) }
        }
    }
}

@Preview(name = "Normal — Dark", showBackground = true, backgroundColor = 0xFF0D0D0D, widthDp = 390)
@Composable
private fun PreviewNormalDark() {
    LaskTheme(darkTheme = true) {
        Column(modifier = Modifier.background(MaterialTheme.LaskColors.backgroundPrimary).padding(vertical = 8.dp)) {
            Section("С картинкой / позитивная тональность") { PreviewCard(articleWithImage) }
            Section("Без картинки / негативная тональность") { PreviewCard(articleNoImage) }
            Section("С картинкой / нейтральная / без автора") { PreviewCard(articleNeutral) }
            Section("Длинный заголовок") { PreviewCard(articleLongTitle) }
            Section("Нет категории, автора, тональности") { PreviewCard(articleNoMeta) }
        }
    }
}

// ── Previews — Edit mode ──────────────────────────────────────────────────────

@Preview(name = "Edit — all kept — Light", showBackground = true, widthDp = 390)
@Composable
private fun PreviewEditAllKeptLight() {
    LaskTheme(darkTheme = false) {
        Column(modifier = Modifier.background(MaterialTheme.LaskColors.backgroundPrimary).padding(vertical = 8.dp)) {
            Section("Edit: все помечены как «сохранить»") {
                PreviewCard(articleWithImage, selectionMode = true, isKept = true)
                PreviewCard(articleNoImage, selectionMode = true, isKept = true)
                PreviewCard(articleNeutral, selectionMode = true, isKept = true)
            }
        }
    }
}

@Preview(name = "Edit — mixed — Light", showBackground = true, widthDp = 390)
@Composable
private fun PreviewEditMixedLight() {
    LaskTheme(darkTheme = false) {
        Column(modifier = Modifier.background(MaterialTheme.LaskColors.backgroundPrimary).padding(vertical = 8.dp)) {
            Section("Edit: жёлтая = оставить, серая = удалить") {
                PreviewCard(articleWithImage, selectionMode = true, isKept = true)
                PreviewCard(articleNoImage, selectionMode = true, isKept = false)
                PreviewCard(articleNeutral, selectionMode = true, isKept = true)
            }
        }
    }
}

@Preview(name = "Edit — all kept — Dark", showBackground = true, backgroundColor = 0xFF0D0D0D, widthDp = 390)
@Composable
private fun PreviewEditAllKeptDark() {
    LaskTheme(darkTheme = true) {
        Column(modifier = Modifier.background(MaterialTheme.LaskColors.backgroundPrimary).padding(vertical = 8.dp)) {
            Section("Edit: все помечены как «сохранить»") {
                PreviewCard(articleWithImage, selectionMode = true, isKept = true)
                PreviewCard(articleNoImage, selectionMode = true, isKept = true)
                PreviewCard(articleNeutral, selectionMode = true, isKept = true)
            }
        }
    }
}

@Preview(name = "Edit — mixed — Dark", showBackground = true, backgroundColor = 0xFF0D0D0D, widthDp = 390)
@Composable
private fun PreviewEditMixedDark() {
    LaskTheme(darkTheme = true) {
        Column(modifier = Modifier.background(MaterialTheme.LaskColors.backgroundPrimary).padding(vertical = 8.dp)) {
            Section("Edit: жёлтая = оставить, серая = удалить") {
                PreviewCard(articleWithImage, selectionMode = true, isKept = true)
                PreviewCard(articleNoImage, selectionMode = true, isKept = false)
                PreviewCard(articleNeutral, selectionMode = true, isKept = true)
            }
        }
    }
}

// ── Preview — isRead ──────────────────────────────────────────────────────────

@Preview(name = "Read badge — Light", showBackground = true, widthDp = 390)
@Composable
private fun PreviewReadLight() {
    LaskTheme(darkTheme = false) {
        Column(modifier = Modifier.background(MaterialTheme.LaskColors.backgroundPrimary).padding(vertical = 8.dp)) {
            Section("Прочитанная статья") {
                PreviewCard(articleWithImage, isRead = true)
                PreviewCard(articleNoImage, isRead = true)
            }
        }
    }
}