package dev.alexmester.domain.progress

import java.time.LocalDate
import java.time.format.DateTimeParseException

data class Streak(
    val count: Int = 0,
    val lastDate: String? = null,
) {
    /** Серия после активности в день [today] (формат yyyy-MM-dd). */
    fun record(today: String): Streak = Streak(
        count = when {
            lastDate == null -> 1
            lastDate == today -> count
            isYesterday(lastDate, today) -> count + 1
            else -> 1
        },
        lastDate = today,
    )

    private fun isYesterday(last: String, today: String): Boolean = try {
        LocalDate.parse(last).plusDays(1) == LocalDate.parse(today)
    } catch (e: DateTimeParseException) {
        false
    }
}