package telegram.webhook

import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton

/**
 * Проверка секретного токена webhook.
 *
 * Telegram умеет присылать заголовок `X-Telegram-Bot-Api-Secret-Token`,
 * если при `setWebhook` был передан параметр `secret_token`.
 *
 * Дополнительно поддерживаем секрет в самом URL webhook.
 * Это полезно для локальной разработки и туннелей, где заголовок может не дойти.
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
