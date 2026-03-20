package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Кэш новостных статей из /top-news и /search-news.
 *
 * [cachedAt] — unix timestamp (ms) момента сохранения.
 * Используется для отображения "обновлено X минут назад" в UI.
 *
 * [sourceScreen] — откуда пришла статья (FEED, SEARCH).
 * Нужно чтобы при pull-to-refresh в ленте не затирать результаты поиска.
 */
@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val text: String?,
    val summary: String?,
    val url: String,
    val image: String?,
    val video: String?,
    val publishDate: String,
    val authors: String,           // JSON-сериализованный List<String> через TypeConverter
    val category: String?,
    val language: String?,
    val sourceCountry: String?,
    val sentiment: Double?,
    val cachedAt: Long,            // System.currentTimeMillis()
    val sourceScreen: String,      // NewsArticleEntity.Source.name()
) {
    companion object {
        const val SOURCE_FEED = "FEED"
        const val SOURCE_SEARCH = "SEARCH"
    }
}