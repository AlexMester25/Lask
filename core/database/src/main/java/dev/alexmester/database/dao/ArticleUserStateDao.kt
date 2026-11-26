package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.ArticleUserStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleUserStateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(state: ArticleUserStateEntity)

    // ── Bookmark ──────────────────────────────────────────────────────────────

    @Query(
        """
    UPDATE article_user_state
    SET isBookmarked = NOT isBookmarked,
        bookmarkedAt = CASE WHEN isBookmarked = 0 THEN :now ELSE NULL END
    WHERE articleId = :articleId
"""
    )
    suspend fun toggleBookmark(articleId: Long, now: Long)

    @Query(
        """
    UPDATE article_user_state
    SET isBookmarked = 0, bookmarkedAt = NULL
    WHERE articleId IN (:ids)
"""
    )
    suspend fun clearBookmarks(ids: Set<Long>)

    /**
     * Удаляет записи состояния, в которых не осталось пользовательских данных
     * (не прочитано, не аплодировано, не в закладках).
     *
     * Вызывать после снятия закладки: иначе строка остаётся пустой и мешает
     * удалению статьи (FK на articles без CASCADE).
     */
    @Query(
        """
    DELETE FROM article_user_state
    WHERE articleId IN (:ids)
      AND isBookmarked = 0 AND isRead = 0 AND clapCount = 0
      AND bookmarkedAt IS NULL AND readAt IS NULL
"""
    )
    suspend fun deleteEmptyStates(ids: Set<Long>)

    @Query("SELECT isBookmarked FROM article_user_state WHERE articleId = :articleId")
    fun observeIsBookmarked(articleId: Long): Flow<Boolean?>

    @Query("SELECT isBookmarked FROM article_user_state WHERE articleId = :articleId")
    suspend fun isBookmarked(articleId: Long): Boolean?

    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.isBookmarked = 1
        ORDER BY s.bookmarkedAt DESC
        LIMIT :limit
    """
    )
    fun observeBookmarkedArticles(limit: Int): Flow<List<ArticleEntity>>

    @Query("SELECT COUNT(*) FROM article_user_state WHERE isBookmarked = 1")
    fun observeBookmarkedCount(): Flow<Int>

    // ── Read ──────────────────────────────────────────────────────────────────

    @Query(
        """
    UPDATE article_user_state
    SET isRead = 1, readAt = :readAt
    WHERE articleId = :articleId AND isRead = 0
"""
    )
    suspend fun markAsRead(articleId: Long, readAt: Long): Int

    @Query("SELECT articleId FROM article_user_state WHERE isRead = 1")
    fun observeReadArticleIds(): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM article_user_state WHERE isRead = 1")
    fun observeReadCount(): Flow<Int>

    /**
     * Окно «Прочитанного» с фильтром по категории.
     *
     * [category] == null означает «любая категория», [onlyUncategorized] ограничивает
     * выборку статьями без категории. Два отдельных параметра нужны потому, что
     * отсутствие категории — это конкретная группа, а не «фильтра нет».
     *
     * `s.articleId` в ORDER BY — стабильный tiebreaker: без него статьи с одинаковым
     * `readAt` могли бы перескакивать между страницами при росте окна.
     */
    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.isRead = 1
          AND (:category IS NULL OR a.category = :category)
          AND (:onlyUncategorized = 0 OR a.category IS NULL)
        ORDER BY s.readAt DESC, s.articleId DESC
        LIMIT :limit
    """
    )
    fun observeReadArticles(
        category: String?,
        onlyUncategorized: Boolean,
        limit: Int,
    ): Flow<List<ArticleEntity>>

    /**
     * Фасеты «Прочитанного»: размер каждой категории по всему списку.
     * Нужны для счётчиков на чипах и для решения «есть ли ещё страницы».
     */
    @Query(
        """
        SELECT a.category AS category, COUNT(*) AS count
        FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.isRead = 1
        GROUP BY a.category
        ORDER BY COUNT(*) DESC, a.category ASC
    """
    )
    fun observeReadCategoryCounts(): Flow<List<CategoryCountRow>>

    // ── Clap ──────────────────────────────────────────────────────────────────

    @Query(
        """
        UPDATE article_user_state
        SET clapCount = clapCount + 1
        WHERE articleId = :articleId
    """
    )
    suspend fun incrementClap(articleId: Long)

    @Query("SELECT clapCount FROM article_user_state WHERE articleId = :articleId")
    fun observeClapCount(articleId: Long): Flow<Int?>

    /** Окно «Клапов» с фильтром по категории. Семантика параметров — как у [observeReadArticles]. */
    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.clapCount > 0
          AND (:category IS NULL OR a.category = :category)
          AND (:onlyUncategorized = 0 OR a.category IS NULL)
        ORDER BY s.clapCount DESC, s.articleId DESC
        LIMIT :limit
    """
    )
    fun observeClappedArticles(
        category: String?,
        onlyUncategorized: Boolean,
        limit: Int,
    ): Flow<List<ArticleEntity>>

    /** Фасеты «Клапов»: размер каждой категории по всему списку. */
    @Query(
        """
        SELECT a.category AS category, COUNT(*) AS count
        FROM articles a
        INNER JOIN article_user_state s ON a.id = s.articleId
        WHERE s.clapCount > 0
        GROUP BY a.category
        ORDER BY COUNT(*) DESC, a.category ASC
    """
    )
    fun observeClappedCategoryCounts(): Flow<List<CategoryCountRow>>
}
