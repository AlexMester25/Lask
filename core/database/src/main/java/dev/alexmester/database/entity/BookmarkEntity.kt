package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Закладки пользователя — никогда не инвалидируются автоматически.
 * Удаляются только явным действием пользователя.
 *
 * Дублирует поля NewsArticleEntity намеренно — закладка должна
 * оставаться читаемой даже если кэш ленты был очищен.
 *
 * [bookmarkedAt] — unix timestamp (ms) момента добавления в закладки.
 * Используется для сортировки списка закладок.
 */
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
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
    val bookmarkedAt: Long,        // System.currentTimeMillis()
)
