package telegram.webhook

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update

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
        pathSecretToken: String?,
    ): ProcessingResult {
        val chatId = update.message?.chatId
        val updateId = update.updateId.toLong()

        log.info("event=webhook_received updateId={} chatId={}", updateId, chatId)

        when (accessPolicy.authorize(secretTokenHeader, pathSecretToken)) {
            AccessDecision.ALLOWED -> return processTelegramUpdateUseCase.execute(update)
            AccessDecision.MISCONFIGURED -> {
                log.error("Webhook secret token не настроен: задайте TELEGRAM_WEBHOOK_SECRET перед запуском приложения")
                return ProcessingResult.forbidden()
            }
            AccessDecision.DENIED -> {
                log.warn(
                    "Отклонен запрос к webhook с невалидным secret token. remote-header-present={} path-secret-present={}",
                    !secretTokenHeader.isNullOrBlank(),
                    !pathSecretToken.isNullOrBlank()
                )
                return ProcessingResult.forbidden()
            }
        }
    }
}


