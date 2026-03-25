package telegram.validation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmojiValidationTest {

    @Test
    fun `contains emoji should return true for rocket`() {
        assertTrue(containsEmoji("Project \uD83D\uDE80"))
    }

    @Test
    fun `contains emoji should return false for plain text`() {
        assertFalse(containsEmoji("Project Name"))
    }
}
