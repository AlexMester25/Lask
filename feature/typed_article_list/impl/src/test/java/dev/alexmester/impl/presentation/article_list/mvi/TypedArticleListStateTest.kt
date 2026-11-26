package dev.alexmester.impl.presentation.article_list.mvi

import dev.alexmester.domain.articles.CategoryCount
import dev.alexmester.domain.articles.CategoryFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class TypedArticleListStateTest {

    @Test
    fun `categories put All first with the sum of every facet`() {
        val categories = TypedArticleListState.categoriesOf(
            facets = listOf(
                CategoryCount(category = "Tech", count = 2),
                CategoryCount(category = "Sport", count = 1),
                CategoryCount(category = null, count = 1),
            ),
            selected = CategoryFilter.All,
        )

        assertEquals(
            listOf(
                CategoryFilter.All to 4,
                CategoryFilter.Named("Tech") to 2,
                CategoryFilter.Named("Sport") to 1,
                CategoryFilter.Uncategorized to 1,
            ),
            categories,
        )
    }

    @Test
    fun `facets keep the order given by sql`() {
        val categories = TypedArticleListState.categoriesOf(
            facets = listOf(
                CategoryCount(category = "Tech", count = 5),
                CategoryCount(category = "Sport", count = 1),
            ),
            selected = CategoryFilter.All,
        )

        assertEquals(
            listOf(
                CategoryFilter.All to 6,
                CategoryFilter.Named("Tech") to 5,
                CategoryFilter.Named("Sport") to 1,
            ),
            categories,
        )
    }

    @Test
    fun `selected category that disappeared stays as an empty chip`() {
        val categories = TypedArticleListState.categoriesOf(
            facets = listOf(CategoryCount(category = "Tech", count = 2)),
            selected = CategoryFilter.Named("Sport"),
        )

        assertEquals(
            listOf(
                CategoryFilter.All to 2,
                CategoryFilter.Named("Tech") to 2,
                CategoryFilter.Named("Sport") to 0,
            ),
            categories,
        )
    }

    @Test
    fun `total of All is the sum of facets`() {
        val total = TypedArticleListState.totalOf(
            filter = CategoryFilter.All,
            facets = listOf(
                CategoryCount(category = "Tech", count = 3),
                CategoryCount(category = null, count = 2),
            ),
        )

        assertEquals(5, total)
    }

    @Test
    fun `total of named category uses its own facet`() {
        val total = TypedArticleListState.totalOf(
            filter = CategoryFilter.Named("Tech"),
            facets = listOf(
                CategoryCount(category = "Tech", count = 3),
                CategoryCount(category = "Sport", count = 9),
            ),
        )

        assertEquals(3, total)
    }

    @Test
    fun `total of uncategorized uses the null facet`() {
        val total = TypedArticleListState.totalOf(
            filter = CategoryFilter.Uncategorized,
            facets = listOf(
                CategoryCount(category = "Tech", count = 3),
                CategoryCount(category = null, count = 2),
            ),
        )

        assertEquals(2, total)
    }

    @Test
    fun `total of a missing category is zero`() {
        val total = TypedArticleListState.totalOf(
            filter = CategoryFilter.Named("Sport"),
            facets = listOf(CategoryCount(category = "Tech", count = 3)),
        )

        assertEquals(0, total)
    }

    @Test
    fun `empty state is not reported while loading`() {
        assertEquals(false, TypedArticleListState().isEmpty)
        assertEquals(true, TypedArticleListState(isLoading = false).isEmpty)
    }

    @Test
    fun `empty filtered list is distinguished from empty history`() {
        assertEquals(false, TypedArticleListState(isLoading = false).isFilteredEmpty)

        val filtered = TypedArticleListState(
            isLoading = false,
            selectedFilter = CategoryFilter.Named("Tech"),
        )
        assertEquals(true, filtered.isFilteredEmpty)
    }
}
