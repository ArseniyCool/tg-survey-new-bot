package telegram.webhook

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.services.SurveyService

@Controller("/telegram")
class TelegramWebhookController(
    private val surveyService: SurveyService,
) {

    @Post(uri = "/webhook", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun webhook(@Body update: Update): HttpResponse<Any> {
        val chatId = update.message?.chatId
        val reply = surveyService.handle(update)
        val text = reply.text

        if (chatId == null || text.isNullOrBlank()) {
            return HttpResponse.ok()
        }

        return HttpResponse.ok(SendMessage(chatId.toString(), text))
    }
}