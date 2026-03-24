package telegram.webhook

import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton

/**
 * Проверка секретного токена webhook.
 *
 * Telegram умеет присылать заголовок `X-Telegram-Bot-Api-Secret-Token`,
 * если при `setWebhook` был передан параметр `secret_token`.
 */
@Singleton
class TelegramWebhookSecurity(
    @Property(name = "telegram.webhook.secret-token") private val expectedSecretToken: String,
) {
    fun isConfigured(): Boolean = expectedSecretToken.isNotBlank()

    fun isValid(receivedSecretToken: String?): Boolean {
        if (!isConfigured()) {
            return false
        }

        return receivedSecretToken == expectedSecretToken
    }
}
