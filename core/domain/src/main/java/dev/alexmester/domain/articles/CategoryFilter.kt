package dev.alexmester.domain.articles

/**
 * Фильтр списков «Прочитанное»/«Клапы» по категории статьи.
 *
 * Отдельный тип вместо `String?` нужен потому, что `null` одновременно означал бы
 * «все категории» и «статьи без категории» — на уровне контракта это разные запросы.
 */
sealed interface CategoryFilter {
    /** Все статьи списка, без ограничения по категории. */
    data object All : CategoryFilter

    /** Только статьи без категории (чип «Other»). */
    data object Uncategorized : CategoryFilter

    /** Статьи конкретной категории. */
    data class Named(val name: String) : CategoryFilter
}

/**
 * Сколько статей списка приходится на категорию.
 *
 * Считается в SQL по всему набору, а не по загруженному окну: иначе счётчики
 * на чипах начнут врать при пагинации. [category] == null — статьи без категории.
 */
data class CategoryCount(
    val category: String?,
    val count: Int,
)
