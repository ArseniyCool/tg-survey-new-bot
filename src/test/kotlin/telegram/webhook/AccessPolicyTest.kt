package telegram.webhook

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccessPolicyTest {

    private val security = Security("very-secret-token", true)
    private val policy = AccessPolicy(security)

    @Test
    fun `should allow request when validation is disabled`() {
        val disabledPolicy = AccessPolicy(Security("", false))

        val decision = disabledPolicy.authorize(null, null)

        assertEquals(AccessDecision.ALLOWED, decision)
    }

    @Test
    fun `should reject request when secret token is missing in configuration`() {
        val misconfiguredPolicy = AccessPolicy(Security("", true))

        val decision = misconfiguredPolicy.authorize(null, null)

        assertEquals(AccessDecision.MISCONFIGURED, decision)
    }

    @Test
    fun `should reject request with invalid token`() {
        val decision = policy.authorize("wrong-token", null)

        assertEquals(AccessDecision.DENIED, decision)
    }

    @Test
    fun `should allow request with valid header token`() {
        val decision = policy.authorize("very-secret-token", null)

        assertEquals(AccessDecision.ALLOWED, decision)
    }

    @Test
    fun `should allow request with valid path token`() {
        val decision = policy.authorize(null, "very-secret-token")

        assertEquals(AccessDecision.ALLOWED, decision)
    }
}


