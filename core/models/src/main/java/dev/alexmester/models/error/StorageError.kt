package dev.alexmester.models.error

/**
 * Ошибки локального хранения: SQLite, файловая система, DataStore.
 */
sealed class StorageError : AppError() {

    /** На устройстве закончилось место (SQLITE_FULL). */
    class NoSpace : StorageError()

    /** База данных или таблица заблокированы другой операцией (SQLITE_BUSY / SQLITE_LOCKED). */
    data class Busy(
        override val cause: Throwable? = null
    ) : StorageError()

    /** Нарушено ограничение схемы: внешний ключ, уникальность, NOT NULL. */
    data class Constraint(
        override val cause: Throwable? = null
    ) : StorageError()

    /** Файл базы данных повреждён (SQLITE_CORRUPT). */
    class Corrupted : StorageError()

    /** Ошибка ввода-вывода при чтении или записи. */
    data class Io(
        override val cause: Throwable? = null
    ) : StorageError()

    data class Unknown(
        override val cause: Throwable? = null,
        override val message: String? = null
    ) : StorageError()
}
