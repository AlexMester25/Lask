package dev.alexmester.models.news


data class NewsCluster(
    val id: Int,
    val articles: List<NewsArticle>,
) {
    val leadArticle: NewsArticle
        get() = articles.firstOrNull { it.image != null } ?: articles.first()
}