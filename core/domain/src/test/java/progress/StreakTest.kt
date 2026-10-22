package progress

import dev.alexmester.domain.progress.Streak
import junit.framework.TestCase.assertEquals
import org.junit.Test

class StreakTest {

    @Test
    fun `first activity starts streak at one`() {
        assertEquals(Streak(1, "2026-04-25"), Streak().record("2026-04-25"))
    }

    @Test
    fun `same day does not change count`() {
        assertEquals(Streak(3, "2026-04-25"), Streak(3, "2026-04-25").record("2026-04-25"))
    }

    @Test
    fun `next day increments`() {
        assertEquals(Streak(2, "2026-04-25"), Streak(1, "2026-04-24").record("2026-04-25"))
    }

    @Test
    fun `increments across month boundary`() {
        assertEquals(Streak(3, "2026-05-01"), Streak(2, "2026-04-30").record("2026-05-01"))
    }

    @Test
    fun `skipped day resets to one`() {
        assertEquals(Streak(1, "2026-04-25"), Streak(5, "2026-04-20").record("2026-04-25"))
    }

    @Test
    fun `invalid stored date resets to one`() {
        assertEquals(Streak(1, "2026-04-25"), Streak(5, "garbage").record("2026-04-25"))
    }
}