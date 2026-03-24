package telegram.webhook

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TelegramWebhookSecurityTest {

    @Test
    fun `configured security should accept matching token`() {
        val security = TelegramWebhookSecurity("very-secret-token")

        assertTrue(security.isConfigured())
        assertTrue(security.isValid("very-secret-token"))
    }

    @Test
    fun `configured security should reject wrong token`() {
        val security = TelegramWebhookSecurity("very-secret-token")

        assertFalse(security.isValid("wrong-token"))
        assertFalse(security.isValid(null))
    }

    @Test
    fun `blank configured token should disable validation and reject requests`() {
        val security = TelegramWebhookSecurity("")

        assertFalse(security.isConfigured())
        assertFalse(security.isValid("anything"))
        assertFalse(security.isValid(null))
    }
}
