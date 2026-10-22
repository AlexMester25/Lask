package progress

import dev.alexmester.domain.progress.Progress
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ProgressTest {

    @Test
    fun `below threshold keeps level and accumulates xp`() {
        val result = Progress().addXp(5f)

        assertEquals(1, result.level)
        assertEquals(5f, result.xp, 0.0001f)
    }

    @Test
    fun `exact threshold levels up with zero remainder`() {
        val result = Progress().addXp(Progress.xpForLevel(1))

        assertEquals(2, result.level)
        assertEquals(0f, result.xp, 0.0001f)
    }

    @Test
    fun `xp above threshold levels up and keeps remainder`() {
        val result = Progress().addXp(40f)

        assertEquals(2, result.level)
        assertEquals(30f, result.xp, 0.0001f)
    }

    @Test
    fun `large xp gives multiple level ups`() {
        val result = Progress().addXp(200f)

        assertEquals(4, result.level)
        assertEquals(83f, result.xp, 1f)
    }

    @Test
    fun `xp accumulates between calls`() {
        val result = Progress().addXp(7f).addXp(5f)

        assertEquals(2, result.level)
        assertEquals(2f, result.xp, 0.0001f)
    }

    @Test
    fun `max level does not level further`() {
        val result = Progress(level = Progress.MAX_LEVEL).addXp(1000f)

        assertEquals(Progress(Progress.MAX_LEVEL, 0f), result)
    }

    @Test
    fun `reaching max level drops the remainder`() {
        val result = Progress(level = Progress.MAX_LEVEL - 1).addXp(1_000_000f)

        assertEquals(Progress(Progress.MAX_LEVEL, 0f), result)
    }
}