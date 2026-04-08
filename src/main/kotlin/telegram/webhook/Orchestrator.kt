package telegram.webhook

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.text.ServiceMessages

/**
 * Координирует полный сценарий обработки входящего Telegram webhook.
 */
@Singleton
class Orchestrator(
    private val accessPolicy: AccessPolicy,
    private val processTelegramUpdateUseCase: ProcessTelegramUpdateUseCase,
) {
    private val log = LoggerFactory.getLogger(Orchestrator::class.java)

    fun process(
        update: Update,
        secretTokenHeader: String?,
    ): ProcessingResult {
        val chatId = update.message?.chatId
        val updateId = update.updateId.toLong()

        log.info("event=webhook_received updateId={} chatId={}", updateId, chatId)

        when (accessPolicy.authorize(secretTokenHeader)) {
            AccessDecision.ALLOWED -> return processTelegramUpdateUseCase.execute(update)
            AccessDecision.MISCONFIGURED -> {
                log.error(ServiceMessages.SECRET_MISCONFIGURED_LOG)
                return ProcessingResult.forbidden()
            }
            AccessDecision.DENIED -> {
                log.warn(
                    ServiceMessages.INVALID_SECRET_LOG,
                    !secretTokenHeader.isNullOrBlank()
                )
                return ProcessingResult.forbidden()
            }
        }
    }
}




