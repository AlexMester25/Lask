package dev.alexmester.data

import dev.alexmester.data.error.NetworkErrorMapper
import dev.alexmester.models.error.NetworkError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.serialization.ContentConvertException
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException

class NetworkErrorMapperTest {

    private val mapper = NetworkErrorMapper()
    private val translateMapper = NetworkErrorMapper.translatePlus()

    @Test
    fun `cancellation is rethrown instead of being mapped`() {
        assertThrows(CancellationException::class.java) {
            mapper.map(CancellationException("cancelled"))
        }
    }

    @Test
    fun `already mapped error passes through unchanged`() {
        val error = NetworkError.NoInternet()

        assertSame(error, mapper.map(error))
    }

    @Test
    fun `request timeout maps to timeout`() {
        assertTrue(mapper.map(HttpRequestTimeoutException("http://test", 1_000L)) is NetworkError.Timeout)
    }

    @Test
    fun `socket timeout maps to timeout`() {
        assertTrue(mapper.map(SocketTimeoutException("read timeout")) is NetworkError.Timeout)
    }

    @Test
    fun `connect timeout maps to no internet`() {
        assertTrue(mapper.map(ConnectTimeoutException("connect timeout")) is NetworkError.NoInternet)
    }

    @Test
    fun `unknown host maps to no internet`() {
        assertTrue(mapper.map(UnknownHostException("host")) is NetworkError.NoInternet)
    }

    @Test
    fun `unresolved address maps to no internet`() {
        assertTrue(mapper.map(UnresolvedAddressException()) is NetworkError.NoInternet)
    }

    @Test
    fun `generic io exception maps to no internet`() {
        assertTrue(mapper.map(IOException("connection reset")) is NetworkError.NoInternet)
    }

    @Test
    fun `serialization exception maps to parse error`() {
        val cause = SerializationException("bad json")

        val result = mapper.map(cause)

        assertTrue(result is NetworkError.ParseError)
        assertSame(cause, (result as NetworkError.ParseError).cause)
    }

    @Test
    fun `ktor json conversion error maps to parse error`() {
        val cause = JsonConvertException("bad json", SerializationException("root"))

        val result = mapper.map(cause)

        assertTrue(result is NetworkError.ParseError)
        assertSame(cause, (result as NetworkError.ParseError).cause)
    }

    @Test
    fun `ktor content conversion error maps to parse error`() {
        assertTrue(mapper.map(ContentConvertException("bad body")) is NetworkError.ParseError)
    }

    @Test
    fun `unexpected exception maps to unknown keeping the cause`() {
        val cause = IllegalStateException("boom")

        val result = mapper.map(cause)

        assertTrue(result is NetworkError.Unknown)
        assertSame(cause, (result as NetworkError.Unknown).cause)
        assertEquals("boom", result.message)
    }

    @Test
    fun `payment required status maps to payment required`() {
        assertTrue(mapper.mapHttpStatus(402) is NetworkError.PaymentRequired)
    }

    @Test
    fun `rate limit status keeps retry after seconds`() {
        assertEquals(
            NetworkError.RateLimit(retryAfterSeconds = 120L),
            mapper.mapHttpStatus(429, retryAfter = "120"),
        )
    }

    @Test
    fun `rate limit status without header keeps retry after empty`() {
        assertEquals(NetworkError.RateLimit(), mapper.mapHttpStatus(429))
    }

    @Test
    fun `rate limit ignores http date retry after`() {
        assertEquals(
            NetworkError.RateLimit(),
            mapper.mapHttpStatus(429, retryAfter = "Wed, 21 Oct 2015 07:28:00 GMT"),
        )
    }

    @Test
    fun `bad gateway status maps to bad gateway`() {
        assertTrue(mapper.mapHttpStatus(502) is NetworkError.BadGateway)
    }

    @Test
    fun `client error status maps to http error with description`() {
        assertEquals(
            NetworkError.HttpError(code = 404, message = "Not Found"),
            mapper.mapHttpStatus(404, description = "Not Found"),
        )
    }

    @Test
    fun `server error status maps to http error`() {
        assertEquals(
            NetworkError.HttpError(code = 503, message = "Service Unavailable"),
            mapper.mapHttpStatus(503, description = "Service Unavailable"),
        )
    }

    @Test
    fun `unprocessable entity maps to translate error for translate api`() {
        assertTrue(translateMapper.mapHttpStatus(422) is NetworkError.TranslateError)
    }

    @Test
    fun `translate api falls back to base mapping for other statuses`() {
        assertEquals(
            NetworkError.HttpError(code = 500),
            translateMapper.mapHttpStatus(500),
        )
    }

    @Test
    fun `translate api still maps rate limit by base rules`() {
        assertTrue(translateMapper.mapHttpStatus(429) is NetworkError.RateLimit)
    }
}
