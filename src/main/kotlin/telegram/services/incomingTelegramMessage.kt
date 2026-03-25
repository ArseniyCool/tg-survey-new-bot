package telegram.services

/**
 * Нормализованное представление входящего сообщения Telegram.
 */

import org.telegram.telegrambots.meta.api.objects.Update

data class IncomingTelegramMessage(
    val chatId: Long,
    val rawText: String,
    val normalizedCommand: String? = null,
)

fun parseIncomingTelegramMessage(update: Update): IncomingTelegramMessage? {
    val message = update.message ?: return null
    val text = (message.text ?: message.contact?.phoneNumber)?.trim().orEmpty()

    if (text.isBlank()) {
        return null
    }

    val normalizedCommand = text
        .takeIf { it.startsWith("/") }
        ?.lowercase()
        ?.split(Regex("\\s+"), limit = 2)
        ?.first()
        ?.substringBefore("@")

    return IncomingTelegramMessage(
        chatId = message.chatId,
        rawText = text,
        normalizedCommand = normalizedCommand,
    )
}
