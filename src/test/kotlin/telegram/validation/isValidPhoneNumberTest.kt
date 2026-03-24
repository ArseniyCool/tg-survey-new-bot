package telegram.validation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IsValidPhoneNumberTest {

    @Test
    fun `normalize phone should convert plus seven format to stored format`() {
        assertEquals("88888888888", normalizePhoneNumber("+7 (888) 888-88-88"))
    }

    @Test
    fun `normalize phone should add eight for ten digits`() {
        assertEquals("88888888888", normalizePhoneNumber("8888888888"))
    }

    @Test
    fun `invalid phone should return null after normalization`() {
        assertEquals(null, normalizePhoneNumber("123"))
    }

    @Test
    fun `is valid phone number should detect valid and invalid values`() {
        assertTrue(isValidPhoneNumber("8 888 888 88 88"))
        assertFalse(isValidPhoneNumber("hello"))
    }
}
