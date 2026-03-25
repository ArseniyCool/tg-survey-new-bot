package telegram.webhook

import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton

/**
 * Компонент проверки секретного токена Telegram webhook.
 *
 * Поддерживает проверку токена из HTTP-заголовка и из параметра пути.
 */
@Singleton
class TelegramWebhookSecurity(
    @Property(name = "telegram.webhook.secret-token") private val expectedSecretToken: String,
    @Property(name = "telegram.webhook.validate-secret") private val validateSecretToken: Boolean,
) {
    fun shouldValidate(): Boolean = validateSecretToken

    fun isConfigured(): Boolean = expectedSecretToken.isNotBlank()

    fun isValid(receivedSecretToken: String?, pathSecretToken: String? = null): Boolean {
        if (!shouldValidate()) {
            return true
        }

        if (!isConfigured()) {
            return false
        }

        return receivedSecretToken == expectedSecretToken || pathSecretToken == expectedSecretToken
    }
}
