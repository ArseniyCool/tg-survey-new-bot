package telegram.model

/**
 * Ответ бота: текст плюс опциональная клавиатура.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

/**
 * Неизменяемая модель ответа, которую возвращает бизнес-логика.
 */
data class BotReply(
    val text: String? = null,
    val replyMarkup: ReplyKeyboard? = null,
)

/**
 * Изменяемый builder, который удобно наполнять внутри обработчиков.
 */
class MutableBotReply {
    var text: String? = null
    var replyMarkup: ReplyKeyboard? = null

    fun toImmutable(): BotReply = BotReply(text = text, replyMarkup = replyMarkup)
}
