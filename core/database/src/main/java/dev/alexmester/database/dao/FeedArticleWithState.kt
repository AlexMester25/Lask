package dev.alexmester.database.dao

data class FeedArticleWithState(
    val id: Long,
    val title: String,
    val text: String?,
    val summary: String?,
    val url: String,
    val image: String?,
    val video: String?,
    val publishDate: String,
    val authors: String,
    val category: String?,
    val language: String?,
    val sourceCountry: String?,
    val sentiment: Double?,
    val clusterId: Int,
    val position: Int,
)
