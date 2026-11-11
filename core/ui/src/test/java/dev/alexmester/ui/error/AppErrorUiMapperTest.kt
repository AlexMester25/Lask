package dev.alexmester.ui.error

import dev.alexmester.models.error.AppError
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.error.StorageError
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import org.junit.Assert.assertEquals
import org.junit.Test

class AppErrorUiMapperTest {

    private fun stringRes(error: AppError): Int =
        (AppErrorUiMapper.toUiText(error) as UiText.StringResource).resId

    @Test
    fun `no space maps to storage full message`() {
        assertEquals(R.string.error_storage_full, stringRes(StorageError.NoSpace()))
    }

    @Test
    fun `busy storage maps to busy message`() {
        assertEquals(R.string.error_storage_busy, stringRes(StorageError.Busy()))
    }

    @Test
    fun `corrupted storage maps to corrupted message`() {
        assertEquals(R.string.error_storage_corrupted, stringRes(StorageError.Corrupted()))
    }

    @Test
    fun `constraint and io errors map to generic storage message`() {
        listOf(
            StorageError.Constraint(),
            StorageError.Io(),
            StorageError.Unknown(),
        ).forEach { error ->
            assertEquals(R.string.error_storage_failed, stringRes(error))
        }
    }

    @Test
    fun `network errors keep their messages`() {
        assertEquals(R.string.error_no_internet, stringRes(NetworkError.NoInternet()))
        assertEquals(R.string.error_timeout, stringRes(NetworkError.Timeout()))
    }
}
