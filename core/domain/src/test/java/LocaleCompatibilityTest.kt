import dev.alexmester.domain.locale.CompatibilityWarning
import dev.alexmester.domain.locale.checkCompatibility
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class LocaleCompatibilityTest {

    @Test
    fun `compatible pair returns null`() = assertNull(checkCompatibility("en", "us"))

    @Test
    fun `incompatible pair suggests first country of language and first language of country`() {
        assertEquals(
            CompatibilityWarning(suggestedCountry = "de", suggestedLanguage = "en"),
            checkCompatibility(language = "de", country = "us"),
        )
    }

    @Test
    fun `country with several languages accepts each of them`() {
        assertNull(checkCompatibility("fr", "ch"))
        assertNull(checkCompatibility("de", "ch"))
    }

    @Test
    fun `unknown language is treated as compatible`() = assertNull(checkCompatibility("xx", "us"))

    @Test
    fun `unknown country is treated as compatible`() = assertNull(checkCompatibility("en", "zz"))
}