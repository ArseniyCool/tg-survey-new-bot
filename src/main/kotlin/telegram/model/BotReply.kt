package telegram.model

/**
 * Модель ответа бота: текст + опциональная клавиатура (разметка ответа / reply markup).
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

/**
 * То, что мы хотим вернуть обратно в Telegram: текст + (опционально) клавиатура.
 */
data class BotReply(
    val text: String? = null,
    val replyMarkup: ReplyKeyboard? = null,
)

/**
 * Изменяемый (mutable) билдер, который используют обработчики команд.
 */
class MutableBotReply {
    var text: String? = null
    var replyMarkup: ReplyKeyboard? = null

    fun toImmutable(): BotReply = BotReply(text = text, replyMarkup = replyMarkup)
}
