package telegram.webhook

/**
 * Сборка HTTP-ответов для Telegram webhook.
 *
 * Здесь в одном месте лежит правило:
 * - если отвечать нечем, просто отдаем 200 OK
 * - если есть текст, возвращаем SendMessage с HTML parse mode
 */

import io.micronaut.http.HttpResponse
import jakarta.inject.Singleton
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

@Singleton
class TelegramWebhookResponder {

    fun ok(chatId: Long?, text: String?, replyMarkup: ReplyKeyboard? = null): HttpResponse<Any> {
        if (chatId == null || text.isNullOrBlank()) {
            return HttpResponse.ok()
        }

        return HttpResponse.ok(
            SendMessage(chatId.toString(), text).apply {
                parseMode = ParseMode.HTML
                this.replyMarkup = replyMarkup
            }
        )
    }
}
