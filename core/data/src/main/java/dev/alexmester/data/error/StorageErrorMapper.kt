package dev.alexmester.data.error

import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import android.database.sqlite.SQLiteReadOnlyDatabaseException
import android.database.sqlite.SQLiteTableLockedException
import dev.alexmester.models.error.StorageError
import java.io.IOException

/**
 * Переводит исключения локального хранилища в доменные [StorageError].
 *
 * Android разворачивает коды SQLite в отдельные типы исключений
 * (SQLITE_FULL -> SQLiteFullException, SQLITE_BUSY/LOCKED -> SQLiteDatabaseLockedException,
 * SQLITE_CONSTRAINT -> SQLiteConstraintException и т.д.), поэтому маппинг идёт
 * по типу, а не по числовому коду.
 */
object StorageErrorMapper {

    fun map(throwable: Throwable): StorageError = when (throwable) {
        is StorageError -> throwable
        is SQLiteFullException -> StorageError.NoSpace()
        is SQLiteDatabaseCorruptException -> StorageError.Corrupted()
        is SQLiteDatabaseLockedException,
        is SQLiteTableLockedException -> StorageError.Busy(throwable)
        is SQLiteConstraintException -> StorageError.Constraint(throwable)
        is SQLiteDiskIOException,
        is SQLiteCantOpenDatabaseException,
        is SQLiteReadOnlyDatabaseException -> StorageError.Io(throwable)
        is IOException -> StorageError.Io(throwable)
        else -> StorageError.Unknown(throwable)
    }
}
