package dev.alexmester.database.dao

/** Проекция `GROUP BY category`: сколько статей списка приходится на категорию. */
data class CategoryCountRow(
    val category: String?,
    val count: Int,
)
