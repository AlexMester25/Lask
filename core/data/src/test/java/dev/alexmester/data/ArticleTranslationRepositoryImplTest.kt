package dev.alexmester.data

import android.database.sqlite.SQLiteFullException
import dev.alexmester.data.repository.ArticleTranslationRepositoryImpl
import dev.alexmester.database.dao.ArticleTranslationDao
import dev.alexmester.database.entity.ArticleTranslationEntity
import dev.alexmester.models.error.StorageError
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.translation.ArticleTranslation
import dev.alexmester.platform.dispatchers.DispatcherProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ArticleTranslationRepositoryImplTest {

    private val dispatchers = object : DispatcherProvider {
        override val main = Dispatchers.Unconfined
        override val io = Dispatchers.Unconfined
        override val default = Dispatchers.Unconfined
    }

    private fun repository(dao: ArticleTranslationDao) =
        ArticleTranslationRepositoryImpl(translationDao = dao, dispatchers = dispatchers)

    @Test
    fun `saved translation is returned for the same article and language`() = runTest {
        val dao = FakeArticleTranslationDao()
        val repository = repository(dao)

        repository.saveTranslation(1L, "ru", translation())

        assertEquals(translation(), repository.getTranslation(1L, "ru"))
    }

    @Test
    fun `translation is not returned for another target language`() = runTest {
        val dao = FakeArticleTranslationDao()
        val repository = repository(dao)

        repository.saveTranslation(1L, "ru", translation())

        assertNull(repository.getTranslation(1L, "de"))
    }

    @Test
    fun `translation is not returned for another article`() = runTest {
        val dao = FakeArticleTranslationDao()
        val repository = repository(dao)

        repository.saveTranslation(1L, "ru", translation())

        assertNull(repository.getTranslation(2L, "ru"))
    }

    @Test
    fun `saving again for the same key replaces the cached translation`() = runTest {
        val dao = FakeArticleTranslationDao()
        val repository = repository(dao)

        repository.saveTranslation(1L, "ru", translation())
        repository.saveTranslation(1L, "ru", translation(title = "Новый заголовок"))

        assertEquals(
            "Новый заголовок",
            repository.getTranslation(1L, "ru")?.title,
        )
        assertEquals(1, dao.rows.size)
    }

    @Test
    fun `save returns storage error when dao fails`() = runTest {
        val repository = repository(
            FakeArticleTranslationDao(saveFailure = SQLiteFullException("full")),
        )

        val result = repository.saveTranslation(1L, "ru", translation())

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is StorageError.NoSpace)
    }

    private fun translation(title: String = "Translated title") = ArticleTranslation(
        title = title,
        text = "Translated text",
    )
}

private class FakeArticleTranslationDao(
    private val saveFailure: Throwable? = null,
) : ArticleTranslationDao {

    val rows = mutableMapOf<Pair<Long, String>, ArticleTranslationEntity>()

    override suspend fun getTranslation(
        articleId: Long,
        targetLanguage: String,
    ): ArticleTranslationEntity? = rows[articleId to targetLanguage]

    override suspend fun upsertTranslation(translation: ArticleTranslationEntity) {
        saveFailure?.let { throw it }
        rows[translation.articleId to translation.targetLanguage] = translation
    }
}
