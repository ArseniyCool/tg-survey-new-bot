package telegram.persistence

/**
 * Заглушка записи анкеты, когда окружение db выключено.
 */

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import telegram.model.SurveyDraft

@Singleton
@Requires(notEnv = ["db"])
class NoopSurveySubmissionWriter : SurveySubmissionWriter {
    override fun write(chatId: Long, draft: SurveyDraft) {
        // Intentionally no-op. Enable env "db" to persist submissions.
    }
}