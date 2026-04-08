package dev.alexmester.api.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ArticleListRoute(
    val type: ArticleListType,
)

enum class ArticleListType {
    READ,
    CLAPPED,
}