package telegram.persistence

import io.micronaut.context.annotation.Requires
import io.micronaut.data.connection.annotation.Connectable
import jakarta.inject.Singleton
import java.time.Instant
import telegram.model.SurveyDraft

@Singleton
@Requires(env = ["db"])
open class DbSurveySubmissionWriter(
    private val repository: SurveySubmissionRepository,
) : SurveySubmissionWriter {

    @Connectable
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
