package telegram.persistence

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.Instant

@MappedEntity("survey_submissions")
data class SurveySubmission(
    @field:Id
    val id: Long?,

    val chatId: Long,
    val phone: String,
    val projectName: String,
    val purpose: String,
    val createdAt: Instant,
)