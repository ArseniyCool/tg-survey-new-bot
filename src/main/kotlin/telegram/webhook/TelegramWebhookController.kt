package telegram.webhook

/**
 * Контроллер приема Telegram webhook-обновлений.
 */

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.PathVariable
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.services.ProcessedUpdateStore
import telegram.services.SurveyService

@Controller("/telegram")
class TelegramWebhookController(
    private val surveyService: SurveyService,
    private val webhookSecurity: TelegramWebhookSecurity,
    private val processedUpdateStore: ProcessedUpdateStore,
    private val responder: TelegramWebhookResponder,
) {
    private val log = LoggerFactory.getLogger(TelegramWebhookController::class.java)

    @Post(uri = "/webhook", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun webhook(
        @Body update: Update,
        @Header("X-Telegram-Bot-Api-Secret-Token") secretTokenHeader: String?,
    ): HttpResponse<Any> {
        return handleWebhook(update, secretTokenHeader, null)
    }

    @Post(uri = "/webhook/{pathSecretToken}", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun webhookWithPathSecret(
        @Body update: Update,
        @PathVariable pathSecretToken: String,
        @Header("X-Telegram-Bot-Api-Secret-Token") secretTokenHeader: String?,
    ): HttpResponse<Any> {
        return handleWebhook(update, secretTokenHeader, pathSecretToken)
    }

    private fun handleWebhook(
        update: Update,
        secretTokenHeader: String?,
        pathSecretToken: String?,
    ): HttpResponse<Any> {
        val chatId = update.message?.chatId
        val updateId = update.updateId.toLong()

        log.info("event=webhook_received updateId={} chatId={}", updateId, chatId)

        if (webhookSecurity.shouldValidate() && !webhookSecurity.isConfigured()) {
            log.error("Webhook secret token не настроен: задайте TELEGRAM_WEBHOOK_SECRET перед запуском приложения")
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }

        if (!webhookSecurity.shouldValidate()) {
            log.warn("Проверка secret token для webhook временно отключена")
        } else if (!webhookSecurity.isValid(secretTokenHeader, pathSecretToken)) {
            log.warn(
                "Отклонен запрос к webhook с невалидным secret token. remote-header-present={} path-secret-present={}",
                !secretTokenHeader.isNullOrBlank(),
                !pathSecretToken.isNullOrBlank()
            )
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }

        return try {
            if (!processedUpdateStore.tryAcquire(updateId)) {
                log.info("Повторный update_id={} проигнорирован", updateId)
                return responder.ok(chatId, null)
            }

            val reply = surveyService.handle(update)
            processedUpdateStore.markCompleted(updateId)
            log.info("event=webhook_processed updateId={} chatId={}", updateId, chatId)
            responder.ok(chatId, reply.text, reply.replyMarkup)
        } catch (e: Exception) {
            runCatching { processedUpdateStore.release(updateId) }
            log.error("Ошибка при обработке входящего webhook-обновления от Telegram", e)

            responder.ok(chatId, "?? Произошла ошибка. Попробуйте позже.")
        }
    }
}
