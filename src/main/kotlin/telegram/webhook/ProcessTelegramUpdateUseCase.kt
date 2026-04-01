package telegram.webhook

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.services.ProcessedUpdateStore
import telegram.services.SurveyService
import telegram.text.ServiceMessages

/**
 * Сценарий безопасной обработки одного Telegram update.
 *
 * Инкапсулирует защиту от повторной обработки, завершение обработки и освобождение update после ошибки.
 */
@Singleton
class ProcessTelegramUpdateUseCase(
    private val surveyService: SurveyService,
    private val processedUpdateStore: ProcessedUpdateStore,
) {
    private val log = LoggerFactory.getLogger(ProcessTelegramUpdateUseCase::class.java)

    fun execute(update: Update): ProcessingResult {
        val chatId = update.message?.chatId
        val updateId = update.updateId.toLong()

        return try {
            if (!processedUpdateStore.tryAcquire(updateId)) {
                log.info(ServiceMessages.DUPLICATE_UPDATE_LOG, updateId)
                return ProcessingResult.acknowledged()
            }

            val reply = surveyService.handle(update)
            processedUpdateStore.markCompleted(updateId)
            log.info("event=webhook_processed updateId={} chatId={}", updateId, chatId)
            ProcessingResult.reply(chatId, reply.text, reply.replyMarkup)
        } catch (e: Exception) {
            runCatching { processedUpdateStore.release(updateId) }
            log.error(ServiceMessages.PROCESSING_FAILED_LOG, e)
            ProcessingResult.reply(chatId, ServiceMessages.PROCESSING_FAILED_REPLY)
        }
    }
}




