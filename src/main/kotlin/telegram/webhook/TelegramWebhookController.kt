package telegram.webhook

/**
 * Webhook-контроллер: принимает обновления Telegram и возвращает SendMessage.
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

            // Telegram ожидает 200 OK; если не можем ответить (нет сообщения/чата), просто подтверждаем (ack).
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
            // Никогда не возвращаем 500 в Telegram, иначе он будет повторно присылать тот же update.
            log.error("Ошибка при обработке входящего webhook-обновления от Telegram", e)

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
