package telegram.webhook

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import telegram.webhook.Security.ValidationResult

class SecurityTest {

    @Test
    fun `configured security should accept matching token`() {
        val security = Security("very-secret-token", true)

        assertEquals(ValidationResult.VALID, security.validate("very-secret-token"))
    }

    @Test
    fun `configured security should reject wrong token`() {
        val security = Security("very-secret-token", true)

        assertEquals(ValidationResult.INVALID, security.validate("wrong-token"))
        assertEquals(ValidationResult.INVALID, security.validate(null))
    }

    @Test
    fun `configured security should accept matching path token`() {
        val security = Security("very-secret-token", true)

        assertEquals(ValidationResult.VALID, security.validate(null, "very-secret-token"))
    }

    @Test
    fun `configured security should reject wrong header and wrong path token`() {
        val security = Security("very-secret-token", true)

        assertEquals(ValidationResult.INVALID, security.validate("wrong-token", "also-wrong"))
    }

    @Test
    fun `blank configured token should report misconfigured state`() {
        val security = Security("", true)

        assertEquals(ValidationResult.MISCONFIGURED, security.validate("anything"))
        assertEquals(ValidationResult.MISCONFIGURED, security.validate(null))
    }

    @Test
    fun `disabled validation should allow requests without token`() {
        val security = Security("", false)

        assertEquals(ValidationResult.DISABLED, security.validate(null))
        assertEquals(ValidationResult.DISABLED, security.validate("wrong-token"))
    }
}


