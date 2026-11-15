package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.ArticleTranslationEntity

@Dao
interface ArticleTranslationDao {

    @Query(
        """
        SELECT * FROM article_translations
        WHERE articleId = :articleId AND targetLanguage = :targetLanguage
        """
    )
    suspend fun getTranslation(
        articleId: Long,
        targetLanguage: String,
    ): ArticleTranslationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTranslation(translation: ArticleTranslationEntity)
}
