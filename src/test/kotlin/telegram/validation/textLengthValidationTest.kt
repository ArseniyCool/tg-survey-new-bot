package telegram.validation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextLengthValidationTest {

    @Test
    fun `length in range should return true for boundary values`() {
        assertTrue(isLengthInRange("12345", 5, 30))
        assertTrue(isLengthInRange("1".repeat(30), 5, 30))
    }

    @Test
    fun `length in range should return false outside bounds`() {
        assertFalse(isLengthInRange("1234", 5, 30))
        assertFalse(isLengthInRange("1".repeat(31), 5, 30))
    }
}
