package dev.alexmester.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * TypeConverter для хранения List<String> в одной колонке Room.
 * Используется для поля authors в NewsArticleEntity и BookmarkEntity.
 */
internal class StringListConverter {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromList(list: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), list)

    @TypeConverter
    fun toList(value: String): List<String> =
        json.decodeFromString(ListSerializer(String.serializer()), value)
}