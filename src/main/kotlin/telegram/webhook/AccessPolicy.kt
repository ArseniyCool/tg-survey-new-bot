package telegram.webhook

import jakarta.inject.Singleton
import telegram.webhook.Security.ValidationResult

/**
 * Политика доступа для входящих Telegram webhook-запросов.
 */
@Singleton
class AccessPolicy(
    private val security: Security,
) {
    fun authorize(
        secretTokenHeader: String?,
        pathSecretToken: String?,
    ): AccessDecision {
        return when (security.validate(secretTokenHeader, pathSecretToken)) {
            ValidationResult.DISABLED,
            ValidationResult.VALID,
            -> AccessDecision.ALLOWED

            ValidationResult.MISCONFIGURED -> AccessDecision.MISCONFIGURED
            ValidationResult.INVALID -> AccessDecision.DENIED
        }
    }
}



