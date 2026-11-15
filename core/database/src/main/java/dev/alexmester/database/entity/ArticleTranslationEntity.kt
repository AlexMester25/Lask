package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "article_translations",
    primaryKeys = ["articleId", "targetLanguage"],
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("articleId")],
)
data class ArticleTranslationEntity(
    val articleId: Long,
    val targetLanguage: String,
    val title: String,
    val text: String,
)
