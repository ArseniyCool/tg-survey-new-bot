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
        // Намеренно ничего не делаем. Включите окружение "db", чтобы сохранять анкеты в базу.
    }
}
