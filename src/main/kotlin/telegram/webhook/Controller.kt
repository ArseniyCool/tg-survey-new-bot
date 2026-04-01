package telegram.webhook

/**
 * Контроллер приема Telegram webhook-обновлений.
 */

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import org.telegram.telegrambots.meta.api.objects.Update

@Controller("/telegram")
class Controller(
    private val orchestrator: Orchestrator,
    private val responder: Responder,
) {
    @Post(uri = "/webhook", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun webhook(
        @Body update: Update,
        @Header("X-Telegram-Bot-Api-Secret-Token") secretTokenHeader: String?,
    ): HttpResponse<Any> {
        return responder.toHttpResponse(
            orchestrator.process(update, secretTokenHeader, null)
        )
    }

    @Post(uri = "/webhook/{pathSecretToken}", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun webhookWithPathSecret(
        @Body update: Update,
        @PathVariable pathSecretToken: String,
        @Header("X-Telegram-Bot-Api-Secret-Token") secretTokenHeader: String?,
    ): HttpResponse<Any> {
        return responder.toHttpResponse(
            orchestrator.process(update, secretTokenHeader, pathSecretToken)
        )
    }
}


