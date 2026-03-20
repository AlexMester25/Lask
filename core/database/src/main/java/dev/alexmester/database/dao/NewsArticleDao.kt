package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {

    // ── Чтение ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM news_articles WHERE sourceScreen = :source ORDER BY publishDate DESC")
    fun getArticlesBySource(source: String): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE id = :id")
    suspend fun getArticleById(id: Long): NewsArticleEntity?

    /**
     * Для отображения времени последнего обновления в UI.
     * Возвращает null если кэш пустой.
     */
    @Query("SELECT MAX(cachedAt) FROM news_articles WHERE sourceScreen = :source")
    suspend fun getLastCachedAt(source: String): Long?

    // ── Запись ────────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)

    // ── Очистка ───────────────────────────────────────────────────────────────

    /**
     * Вызывается при pull-to-refresh — чистим только конкретный источник,
     * не затрагивая кэш других экранов.
     */
    @Query("DELETE FROM news_articles WHERE sourceScreen = :source")
    suspend fun clearBySource(source: String)

    @Query("DELETE FROM news_articles")
    suspend fun clearAll()
}
