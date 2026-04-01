package telegram.webhook

import io.micronaut.http.HttpStatus
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

/**
 * Результат обработки входящего Telegram webhook на уровне приложения.
 */
data class ProcessingResult(
    val status: HttpStatus,
    val chatId: Long? = null,
    val text: String? = null,
    val replyMarkup: ReplyKeyboard? = null,
) {
    companion object {
        fun forbidden(): ProcessingResult = ProcessingResult(
            status = HttpStatus.FORBIDDEN,
        )

        fun acknowledged(): ProcessingResult = ProcessingResult(
            status = HttpStatus.OK,
        )

        fun reply(
            chatId: Long?,
            text: String?,
            replyMarkup: ReplyKeyboard? = null,
        ): ProcessingResult = ProcessingResult(
            status = HttpStatus.OK,
            chatId = chatId,
            text = text,
            replyMarkup = replyMarkup,
        )
    }
}


