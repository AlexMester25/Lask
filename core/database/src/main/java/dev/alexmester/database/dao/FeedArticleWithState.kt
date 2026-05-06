package dev.alexmester.database.dao

import androidx.room.Embedded
import dev.alexmester.database.entity.ArticleEntity

data class FeedArticleWithState(
    @Embedded val article: ArticleEntity,
    val clusterId: Int,
    val position: Int,
)
