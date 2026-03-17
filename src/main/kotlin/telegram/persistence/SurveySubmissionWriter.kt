package telegram.persistence

/**
 * Абстракция сохранения анкеты: позволяет подменять реализацию (БД/заглушка/тест).
 */

import telegram.model.SurveyDraft

interface SurveySubmissionWriter {
    fun write(chatId: Long, draft: SurveyDraft)
}