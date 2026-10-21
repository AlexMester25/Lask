import dev.alexmester.domain.interests.InterestError
import dev.alexmester.domain.interests.InterestRules
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class InterestRulesTest {
    private val none = emptySet<String>()

    @Test
    fun `blank input is too short`() = assertEquals(InterestError.TooShort, InterestRules.validate("   ", none))
    @Test fun `too long`() = assertEquals(InterestError.TooLong, InterestRules.validate("a".repeat(31), none))
    @Test fun `digits only has no letters`() = assertEquals(InterestError.NoLetters, InterestRules.validate("123", none))
    @Test fun `unsupported characters`() = assertEquals(InterestError.InvalidCharacters, InterestRules.validate("c#", none))

    @Test
    fun `duplicate is detected after normalization`() =
        assertEquals(InterestError.AlreadyExists, InterestRules.validate("  Kotlin ", setOf("kotlin")))

    @Test
    fun `digits, hyphen, spaces and non-latin letters are allowed`() {
        assertNull(InterestRules.validate("web-3 dev", none))
        assertNull(InterestRules.validate("Космос", none))
    }
}