package dev.alexmester.impl

import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.impl.domain.usecase.TranslateTextsUseCase
import dev.alexmester.models.error.AppError
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.error.StorageError
import dev.alexmester.models.result.AppResult
import dev.alexmester.models.translation.ArticleTranslation
import dev.alexmester.testing.fake.FakeArticleTranslationRepository
import dev.alexmester.testing.fake.TranslationCacheWrite
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TranslateTextsUseCaseTest {

    private val detailRepository = FakeArticleDetailRepository()
    private val translationRepository = FakeArticleTranslationRepository()
    private val useCase = TranslateTextsUseCase(detailRepository, translationRepository)

    @Test
    fun `cached translation is returned without network calls`() = runTest {
        val cached = ArticleTranslation(title = "Кэш", text = "Кэшированный текст")
        translationRepository.cached = cached

        val result = useCase(
            articleId = 1L,
            title = "Title",
            bodyText = "Body",
            targetLanguage = "ru",
            sourceLanguage = "auto",
        )

        assertEquals(AppResult.Success(cached), result)
        assertTrue(detailRepository.calls.isEmpty())
        assertTrue(translationRepository.saved.isEmpty())
    }

    @Test
    fun `missing cache translates title and body and stores result`() = runTest {
        detailRepository.translations = mapOf(
            "Title" to "Заголовок",
            "Body" to "Текст",
        )

        val result = useCase(
            articleId = 1L,
            title = "Title",
            bodyText = "Body",
            targetLanguage = "ru",
            sourceLanguage = "auto",
        )

        assertEquals(
            AppResult.Success(ArticleTranslation(title = "Заголовок", text = "Текст")),
            result,
        )
        assertEquals(listOf("Title", "Body"), detailRepository.calls)
        assertEquals(
            listOf(TranslationCacheWrite(1L, "ru", ArticleTranslation("Заголовок", "Текст"))),
            translationRepository.saved,
        )
    }

    @Test
    fun `failed translation is not cached`() = runTest {
        detailRepository.failure = NetworkError.Unknown()

        val result = useCase(
            articleId = 1L,
            title = "Title",
            bodyText = "Body",
            targetLanguage = "ru",
            sourceLanguage = "auto",
        )

        assertTrue(result is AppResult.Failure)
        assertTrue(translationRepository.saved.isEmpty())
    }

    @Test
    fun `cache save failure does not fail a successful translation`() = runTest {
        translationRepository.saveFailure = StorageError.NoSpace()

        val result = useCase(
            articleId = 1L,
            title = "Title",
            bodyText = "Body",
            targetLanguage = "ru",
            sourceLanguage = "auto",
        )

        assertEquals(
            AppResult.Success(
                ArticleTranslation(
                    title = "translated:Title",
                    text = "translated:Body",
                )
            ),
            result,
        )
    }
}

private class FakeArticleDetailRepository(
    var translations: Map<String, String> = emptyMap(),
    var failure: AppError? = null,
) : ArticleDetailRepository {

    val calls = mutableListOf<String>()

    override suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String,
    ): AppResult<String> {
        calls += text
        failure?.let { return AppResult.Failure(it) }
        return AppResult.Success(translations[text] ?: "translated:$text")
    }
}
