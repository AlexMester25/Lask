package dev.alexmester.api.navigation

import kotlinx.serialization.Serializable

@Serializable
data class TypedArticleListRoute(
    val type: ArticleListType
)

enum class ArticleListType {
    READ,
    CLAPPED,
}