package telegram.webhook

import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton

/**
 * Компонент проверки секретного токена Telegram webhook.
 *
 * Возвращает результат проверки как отдельное значение,
 * чтобы контроллер не собирал решение из нескольких булевых условий.
 */
@Singleton
class Security(
    @Property(name = "telegram.webhook.secret-token") private val expectedSecretToken: String,
    @Property(name = "telegram.webhook.validate-secret") private val validateSecretToken: Boolean,
) {
    enum class ValidationResult {
        DISABLED,
        VALID,
        MISCONFIGURED,
        INVALID,
    }

    fun validate(receivedSecretToken: String?): ValidationResult {
        if (!validateSecretToken) {
            return ValidationResult.DISABLED
        }

        if (expectedSecretToken.isBlank()) {
            return ValidationResult.MISCONFIGURED
        }

        if (receivedSecretToken == expectedSecretToken) {
            return ValidationResult.VALID
        }

        return ValidationResult.INVALID
    }
}


