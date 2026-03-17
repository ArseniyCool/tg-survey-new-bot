package telegram.model

/**
 * Модель ответа бота: текст + опциональная клавиатура (reply markup).
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

/**
 * What we want to send back to Telegram: text + optional keyboard.
 */
data class BotReply(
    val text: String? = null,
    val replyMarkup: ReplyKeyboard? = null,
)

/**
 * Mutable builder used by command handlers.
 */
class MutableBotReply {
    var text: String? = null
    var replyMarkup: ReplyKeyboard? = null

    fun toImmutable(): BotReply = BotReply(text = text, replyMarkup = replyMarkup)
}
