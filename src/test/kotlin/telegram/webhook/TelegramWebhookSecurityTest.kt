package telegram.webhook

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TelegramWebhookSecurityTest {

    @Test
    fun `configured security should accept matching token`() {
        val security = TelegramWebhookSecurity("very-secret-token", true)

        assertTrue(security.isConfigured())
        assertTrue(security.isValid("very-secret-token"))
    }

    @Test
    fun `configured security should reject wrong token`() {
        val security = TelegramWebhookSecurity("very-secret-token", true)

        assertFalse(security.isValid("wrong-token"))
        assertFalse(security.isValid(null))
    }

    @Test
    fun `configured security should accept matching path token`() {
        val security = TelegramWebhookSecurity("very-secret-token", true)

        assertTrue(security.isValid(null, "very-secret-token"))
    }

    @Test
    fun `configured security should reject wrong header and wrong path token`() {
        val security = TelegramWebhookSecurity("very-secret-token", true)

        assertFalse(security.isValid("wrong-token", "also-wrong"))
    }

    @Test
    fun `blank configured token should disable validation and reject requests`() {
        val security = TelegramWebhookSecurity("", true)

        assertFalse(security.isConfigured())
        assertFalse(security.isValid("anything"))
        assertFalse(security.isValid(null))
    }

    @Test
    fun `disabled validation should allow requests without token`() {
        val security = TelegramWebhookSecurity("", false)

        assertFalse(security.shouldValidate())
        assertTrue(security.isValid(null))
        assertTrue(security.isValid("wrong-token"))
    }
}
