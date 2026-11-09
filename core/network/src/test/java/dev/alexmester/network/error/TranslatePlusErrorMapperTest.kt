package dev.alexmester.network.error

import dev.alexmester.models.error.NetworkError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslatePlusErrorMapperTest {

    private val mapper = TranslatePlusErrorMapper()

    @Test
    fun `unprocessable entity maps to translate error`() {
        assertTrue(mapper.mapHttpStatus(422) is NetworkError.TranslateError)
    }

    @Test
    fun `other statuses fall back to base mapping`() {
        assertEquals(NetworkError.HttpError(code = 500), mapper.mapHttpStatus(500))
    }

    @Test
    fun `rate limit is still mapped by base mapper`() {
        assertTrue(mapper.mapHttpStatus(429) is NetworkError.RateLimit)
    }
}
