package dev.alexmester.models.news

data class SearchParams(
    val text: String? = null,
    val textMatchIndexes: List<String>? = null,
    val language: String? = null,
    val sourceCountries: List<String>? = null,
    val categories: List<String>? = null,
    val minSentiment: Double? = null,
    val maxSentiment: Double? = null,
    val earliestPublishDate: String? = null,
    val latestPublishDate: String? = null,
    val authors: List<String>? = null,
    val newsSources: List<String>? = null,
    val locationFilter: String? = null,
    val sort: SortOrder? = null,
    val sortDirection: SortDirection? = null,
    val offset: Int = 0,
    val number: Int = 20,
) {
    enum class SortOrder(val apiValue: String) {
        RELEVANCE("relevance"),
        PUBLISH_TIME("publish-time"),
    }

    enum class SortDirection(val apiValue: String) {
        ASC("ASC"),
        DESC("DESC"),
    }
}

