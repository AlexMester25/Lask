package dev.alexmester.data

import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import android.database.sqlite.SQLiteReadOnlyDatabaseException
import android.database.sqlite.SQLiteTableLockedException
import dev.alexmester.data.error.StorageErrorMapper
import dev.alexmester.data.extension.runStorageResult
import dev.alexmester.models.error.StorageError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException

class StorageErrorMapperTest {

    @Test
    fun `SQLITE_FULL maps to NoSpace`() {
        assertTrue(StorageErrorMapper.map(SQLiteFullException("full")) is StorageError.NoSpace)
    }

    @Test
    fun `corrupted database maps to Corrupted`() {
        assertTrue(
            StorageErrorMapper.map(SQLiteDatabaseCorruptException("corrupt")) is StorageError.Corrupted
        )
    }

    @Test
    fun `locked database and table map to Busy`() {
        val databaseLocked = SQLiteDatabaseLockedException("busy")
        val tableLocked = SQLiteTableLockedException("locked")

        val dbError = StorageErrorMapper.map(databaseLocked)
        val tableError = StorageErrorMapper.map(tableLocked)

        assertTrue(dbError is StorageError.Busy)
        assertSame(databaseLocked, (dbError as StorageError.Busy).cause)
        assertTrue(tableError is StorageError.Busy)
        assertSame(tableLocked, (tableError as StorageError.Busy).cause)
    }

    @Test
    fun `constraint violation maps to Constraint`() {
        val cause = SQLiteConstraintException("FOREIGN KEY constraint failed")

        val error = StorageErrorMapper.map(cause)

        assertTrue(error is StorageError.Constraint)
        assertSame(cause, (error as StorageError.Constraint).cause)
    }

    @Test
    fun `disk and file errors map to Io`() {
        val errors = listOf(
            SQLiteDiskIOException("disk"),
            SQLiteCantOpenDatabaseException("open"),
            SQLiteReadOnlyDatabaseException("read only"),
            IOException("data store"),
        )

        errors.forEach { throwable ->
            val error = StorageErrorMapper.map(throwable)
            assertTrue("$throwable -> $error", error is StorageError.Io)
            assertSame(throwable, (error as StorageError.Io).cause)
        }
    }

    @Test
    fun `unexpected exception maps to Unknown`() {
        val cause = IllegalStateException("boom")

        val error = StorageErrorMapper.map(cause)

        assertTrue(error is StorageError.Unknown)
        assertSame(cause, (error as StorageError.Unknown).cause)
    }

    @Test
    fun `storage error is returned as is`() {
        val error = StorageError.Constraint()

        assertSame(error, StorageErrorMapper.map(error))
    }

    @Test
    fun `runStorageResult returns success`() = runTest {
        val result = runStorageResult { 42 }

        assertEquals(AppResult.Success(42), result)
    }

    @Test
    fun `runStorageResult maps storage exception`() = runTest {
        val result = runStorageResult<Unit> { throw SQLiteFullException("full") }

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is StorageError.NoSpace)
    }

    @Test
    fun `runStorageResult keeps app error`() = runTest {
        val error = StorageError.Constraint()

        val result = runStorageResult<Unit> { throw error }

        assertTrue(result is AppResult.Failure)
        assertSame(error, (result as AppResult.Failure).error)
    }

    @Test
    fun `runStorageResult rethrows cancellation`() = runTest {
        try {
            runStorageResult<Unit> { throw CancellationException("cancelled") }
            fail("CancellationException expected")
        } catch (expected: CancellationException) {
            assertEquals("cancelled", expected.message)
        }
    }
}
