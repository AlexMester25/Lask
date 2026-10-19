package dev.alexmester.impl.data.mapper

import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.models.news.SearchParams

internal fun SearchFilters.toSearchParams(
    query: String,
    offset: Int,
    number: Int,
) = SearchParams(
    text = query,
    language = language,
    sourceCountries = country?.let(::listOf),
    categories = category?.let(::listOf),
    earliestPublishDate = earliestDate,
    latestPublishDate = latestDate,
    sort = SearchParams.SortOrder.PUBLISH_TIME,
    sortDirection = if (sortAscending) SearchParams.SortDirection.ASC
    else SearchParams.SortDirection.DESC,
    offset = offset,
    number = number,
)