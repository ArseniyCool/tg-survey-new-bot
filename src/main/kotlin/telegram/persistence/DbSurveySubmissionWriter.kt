package telegram.persistence

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.time.Instant
import telegram.model.SurveyDraft

@Singleton
@Requires(env = ["db"])
class DbSurveySubmissionWriter(
    private val repository: SurveySubmissionRepository,
) : SurveySubmissionWriter {

    override fun write(chatId: Long, draft: SurveyDraft) {
        repository.save(
            SurveySubmission(
                id = null,
                chatId = chatId,
                phone = draft.phone ?: "",
                projectName = draft.projectName ?: "",
                purpose = draft.purpose ?: "",
                createdAt = Instant.now(),
            )
        )
    }
}