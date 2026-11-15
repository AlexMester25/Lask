package dev.alexmester.data.mapper

import dev.alexmester.database.entity.ArticleTranslationEntity
import dev.alexmester.models.translation.ArticleTranslation

internal fun ArticleTranslationEntity.toDomain() = ArticleTranslation(
    title = title,
    text = text,
)

internal fun ArticleTranslation.toEntity(
    articleId: Long,
    targetLanguage: String,
) = ArticleTranslationEntity(
    articleId = articleId,
    targetLanguage = targetLanguage,
    title = title,
    text = text,
)
