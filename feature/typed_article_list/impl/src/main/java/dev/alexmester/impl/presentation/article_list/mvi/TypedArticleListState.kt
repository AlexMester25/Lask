package dev.alexmester.impl.presentation.article_list.mvi

import dev.alexmester.domain.articles.CategoryCount
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.models.news.NewsArticle

/**
 * Состояние экрана «Прочитанное»/«Клапы».
 *
 * Список и фильтр по категории приходят уже готовыми из SQL: экран держит только
 * текущее окно [articles] и фасеты для чипов. Фильтрация и подсчёт в памяти здесь
 * недопустимы — при пагинации они считались бы по загруженной странице, а не по
 * всему списку, и счётчики на чипах врали бы.
 */
data class TypedArticleListState(
    val articles: List<NewsArticle> = emptyList(),
    /** Чипы категорий: [CategoryFilter] → количество статей во всём списке. */
    val categories: List<Pair<CategoryFilter, Int>> = emptyList(),
    val selectedFilter: CategoryFilter = CategoryFilter.All,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLoading: Boolean = true,
) {
    /** Список пуст вообще, а не из-за выбранной категории. */
    val isEmpty: Boolean
        get() = !isLoading && articles.isEmpty()

    /** Выбранная категория пуста, хотя другие статьи есть. */
    val isFilteredEmpty: Boolean
        get() = isEmpty && selectedFilter != CategoryFilter.All

    companion object {
        /**
         * Собирает чипы из фасетов: «All» всегда первым и со суммой по всем
         * категориям, дальше — категории в порядке, который задал SQL
         * (`COUNT(*) DESC, category ASC`).
         *
         * Если выбранная категория исчезла из фасетов (последнюю статью удалили),
         * чип остаётся с нулём: иначе пользователь увидел бы пустой список без
         * выбранного фильтра и не смог бы вернуться к «All» одним нажатием.
         */
        fun categoriesOf(
            facets: List<CategoryCount>,
            selected: CategoryFilter,
        ): List<Pair<CategoryFilter, Int>> {
            val chips = facets.map { facet -> facet.category.toFilter() to facet.count }
            return buildList(chips.size + 2) {
                add(CategoryFilter.All to facets.sumOf { it.count })
                addAll(chips)
                if (selected != CategoryFilter.All && chips.none { it.first == selected }) {
                    add(selected to 0)
                }
            }
        }

        /** Сколько всего статей у выбранного фильтра — по фасетам, а не по окну. */
        fun totalOf(filter: CategoryFilter, facets: List<CategoryCount>): Int = when (filter) {
            CategoryFilter.All -> facets.sumOf { it.count }
            CategoryFilter.Uncategorized -> facets.firstOrNull { it.category == null }?.count ?: 0
            is CategoryFilter.Named -> facets.firstOrNull { it.category == filter.name }?.count ?: 0
        }
    }
}

private fun String?.toFilter(): CategoryFilter =
    if (this == null) CategoryFilter.Uncategorized else CategoryFilter.Named(this)
