package telegram.webhook

/**
 * Webhook-контроллер: принимает обновления Telegram и возвращает SendMessage в ответ.
 *
 * Важно:
 * - Telegram ожидает 200 OK. Если вернуть 500, Telegram будет ретраить тот же update.
 * - Поэтому даже при ошибках мы стараемся отвечать 200 OK.
 */

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.services.SurveyService

@Controller("/telegram")
class TelegramWebhookController(
    private val surveyService: SurveyService,
) {

    private val log = LoggerFactory.getLogger(TelegramWebhookController::class.java)

    @Post(uri = "/webhook", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun webhook(@Body update: Update): HttpResponse<Any> {
        val chatId = update.message?.chatId

        return try {
            val reply = surveyService.handle(update)
            val text = reply.text

            // Если мы не можем ответить (нет message/chat), просто подтверждаем (ack).
            if (chatId == null || text.isNullOrBlank()) {
                HttpResponse.ok()
            } else {
                HttpResponse.ok(
                    SendMessage(chatId.toString(), text).apply {
                        parseMode = ParseMode.HTML
                        replyMarkup = reply.replyMarkup
                    }
                )
            }
        } catch (e: Exception) {
            log.error("Ошибка при обработке входящего webhook-обновления от Telegram", e)

            // Даже при ошибках возвращаем 200 OK, чтобы Telegram не ретраил update бесконечно.
            if (chatId == null) {
                HttpResponse.ok()
            } else {
                HttpResponse.ok(
                    SendMessage(chatId.toString(), "⚠️ Произошла ошибка. Попробуйте позже.").apply {
                        parseMode = ParseMode.HTML
                    }
                )
            }
        }
    }
}

