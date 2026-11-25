package dev.alexmester.impl.presentation.article_list.mvi

import dev.alexmester.models.news.NewsArticle

/**
 * Состояние экрана «Прочитанное»/«Клапы».
 *
 * [categories] и [filteredArticles] — производные от [allArticles] и [selectedCategory].
 * Они кэшируются на время жизни конкретного экземпляра состояния: без этого
 * `groupBy` и `filter` пересчитывались бы на каждое чтение, то есть на каждую
 * рекомпозицию списка.
 */
data class TypedArticleListState(
    val allArticles: List<NewsArticle> = emptyList(),
    val selectedCategory: String? = null, // null = All
    val isLoading: Boolean = true,
) {
    private val categoriesCache: List<Pair<String?, Int>> by lazy { buildCategories() }
    private val filteredArticlesCache: List<NewsArticle> by lazy { buildFilteredArticles() }

    /**
     * Категории вычисляются из статей.
     * Статьи без категории попадают в "Other".
     */
    val categories: List<Pair<String?, Int>>
        get() = categoriesCache

    val filteredArticles: List<NewsArticle>
        get() = filteredArticlesCache

    private fun buildCategories(): List<Pair<String?, Int>> {
        val grouped = allArticles.groupBy { it.category ?: OTHER_CATEGORY }
        return buildList(grouped.size + 1) {
            // "All" первым
            add(null to allArticles.size)
            grouped.entries
                .sortedByDescending { it.value.size }
                .forEach { (category, articles) ->
                    add(category to articles.size)
                }
        }
    }

    private fun buildFilteredArticles(): List<NewsArticle> = when (selectedCategory) {
        null -> allArticles
        OTHER_CATEGORY -> allArticles.filter { it.category == null }
        else -> allArticles.filter { it.category == selectedCategory }
    }

    companion object {
        const val OTHER_CATEGORY = "Other"
    }
}
