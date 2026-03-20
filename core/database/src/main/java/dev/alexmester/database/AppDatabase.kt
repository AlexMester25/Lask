package dev.alexmester.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.alexmester.database.converter.StringListConverter
import dev.alexmester.database.dao.BookmarkDao
import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.entity.BookmarkEntity
import dev.alexmester.database.entity.NewsArticleEntity

@Database(
    entities = [
        NewsArticleEntity::class,
        BookmarkEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsArticleDao(): NewsArticleDao
    abstract fun bookmarkDao(): BookmarkDao
}