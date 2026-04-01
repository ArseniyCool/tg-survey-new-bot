package telegram.webhook

/**
 * Вспомогательный компонент преобразования результата обработки webhook в HTTP-ответ.
 */

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import jakarta.inject.Singleton
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Singleton
class Responder {

    fun toHttpResponse(result: ProcessingResult): HttpResponse<Any> {
        if (result.status != HttpStatus.OK) {
            return HttpResponse.status(result.status)
        }

        if (result.chatId == null || result.text.isNullOrBlank()) {
            return HttpResponse.ok()
        }

        return HttpResponse.ok(
            SendMessage(result.chatId.toString(), result.text).apply {
                parseMode = ParseMode.HTML
                replyMarkup = result.replyMarkup
            }
        )
    }
}


