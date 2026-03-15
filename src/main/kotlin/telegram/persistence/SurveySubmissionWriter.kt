package telegram.persistence

import telegram.model.SurveyDraft

interface SurveySubmissionWriter {
    fun write(chatId: Long, draft: SurveyDraft)
}