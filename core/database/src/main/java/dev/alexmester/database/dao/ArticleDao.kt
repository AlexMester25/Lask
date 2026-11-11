package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.ArticleEntity

@Dao
interface ArticleDao {

    /**
     * IGNORE — не перезаписываем существующие статьи.
     * Это критично: REPLACE делает DELETE+INSERT, что срабатывает CASCADE
     * и уничтожает article_user_state.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: Long): ArticleEntity?

    /**
     * Удаляем записи состояния, в которых нет пользовательских данных
     * (не прочитано, не аплодировано, не в закладках) у статей, выпавших из всех лент.
     *
     * Вызывать перед [deleteOrphaned] в той же транзакции: FK article_user_state -> articles
     * объявлен без CASCADE, поэтому такая строка блокирует удаление статьи
     * (SQLITE_CONSTRAINT_FOREIGNKEY) и откатывает весь refresh.
     */
    @Query("""
        DELETE FROM article_user_state
        WHERE isBookmarked = 0 AND isRead = 0 AND clapCount = 0
        AND bookmarkedAt IS NULL AND readAt IS NULL
        AND articleId NOT IN (SELECT articleId FROM feed_cache)
    """)
    suspend fun deleteOrphanedInactiveUserStates()

    /**
     * Удаляем статьи у которых:
     * - нет записи в feed_cache (не нужны ни одной ленте)
     * - нет активного user state (не прочитано, не аплодировано, не в закладках)
     *
     * Вызывать после [deleteOrphanedInactiveUserStates] при refresh ленты.
     */
    @Query("""
        DELETE FROM articles
        WHERE id NOT IN (SELECT articleId FROM feed_cache)
        AND id NOT IN (
            SELECT articleId FROM article_user_state
            WHERE isBookmarked = 1 OR isRead = 1 OR clapCount > 0
        )
    """)
    suspend fun deleteOrphaned()
}