package dev.alexmester.domain.progress

import kotlin.math.pow

data class Progress(
    val level: Int = MIN_LEVEL,
    val xp: Float = 0f,
) {
    fun addXp(delta: Float): Progress {
        var newXp = xp + delta
        var newLevel = level

        while (newLevel < MAX_LEVEL) {
            val needed = xpForLevel(newLevel)
            if (needed <= 0f || newXp < needed) break
            newXp -= needed
            newLevel++
        }

        return if (newLevel >= MAX_LEVEL) Progress(MAX_LEVEL, 0f) else Progress(newLevel, newXp)
    }

    companion object {
        const val MIN_LEVEL = 1
        const val MAX_LEVEL = 50

        /** XP, необходимое для перехода с [level] на следующий уровень. */
        fun xpForLevel(level: Int): Float = (10.0 * level.toDouble().pow(1.8)).toFloat()
    }
}