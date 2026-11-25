package dev.alexmester.impl.presentation.article_list.mvi

import dev.alexmester.testing.builder.buildArticle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class TypedArticleListStateTest {

    @Test
    fun `categories count articles and keep All first`() {
        val state = TypedArticleListState(
            allArticles = listOf(
                buildArticle(id = 1, category = "Tech"),
                buildArticle(id = 2, category = "Tech"),
                buildArticle(id = 3, category = "Sport"),
                buildArticle(id = 4, category = null),
            ),
        )

        assertEquals(
            listOf(
                null to 4,
                "Tech" to 2,
                "Sport" to 1,
                "Other" to 1,
            ),
            state.categories,
        )
    }

    @Test
    fun `all category returns every article`() {
        val articles = listOf(
            buildArticle(id = 1, category = "Tech"),
            buildArticle(id = 2, category = null),
        )
        val state = TypedArticleListState(allArticles = articles, selectedCategory = null)

        assertEquals(articles, state.filteredArticles)
    }

    @Test
    fun `other category returns only articles without category`() {
        val state = TypedArticleListState(
            allArticles = listOf(
                buildArticle(id = 1, category = "Tech"),
                buildArticle(id = 2, category = null),
            ),
            selectedCategory = TypedArticleListState.OTHER_CATEGORY,
        )

        assertEquals(listOf(2L), state.filteredArticles.map { it.id })
    }

    @Test
    fun `specific category returns matching articles`() {
        val state = TypedArticleListState(
            allArticles = listOf(
                buildArticle(id = 1, category = "Tech"),
                buildArticle(id = 2, category = "Sport"),
                buildArticle(id = 3, category = "Tech"),
            ),
            selectedCategory = "Tech",
        )

        assertEquals(listOf(1L, 3L), state.filteredArticles.map { it.id })
    }

    @Test
    fun `derived values are computed once per state instance`() {
        val state = TypedArticleListState(
            allArticles = listOf(buildArticle(id = 1, category = "Tech")),
            selectedCategory = "Tech",
        )

        assertSame(state.categories, state.categories)
        assertSame(state.filteredArticles, state.filteredArticles)
    }

    @Test
    fun `changing category produces new filtered articles`() {
        val state = TypedArticleListState(
            allArticles = listOf(
                buildArticle(id = 1, category = "Tech"),
                buildArticle(id = 2, category = "Sport"),
            ),
        )

        assertEquals(listOf(1L), state.copy(selectedCategory = "Tech").filteredArticles.map { it.id })
        assertEquals(listOf(2L), state.copy(selectedCategory = "Sport").filteredArticles.map { it.id })
    }
}
