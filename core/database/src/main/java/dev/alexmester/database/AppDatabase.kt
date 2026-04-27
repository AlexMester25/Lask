package dev.alexmester.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.alexmester.database.converter.StringListConverter
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.di.DatabaseConstants.DB_VERSION
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.ArticleUserStateEntity
import dev.alexmester.database.entity.FeedCacheEntity

@Database(
    entities = [
        ArticleEntity::class,
        ArticleUserStateEntity::class,
        FeedCacheEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun articleUserStateDao(): ArticleUserStateDao
    abstract fun feedCacheDao(): FeedCacheDao
}