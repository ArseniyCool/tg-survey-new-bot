package telegram.persistence

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import java.time.Instant

@MappedEntity("survey_submissions")
data class SurveySubmission(
    @field:Id
    @field:GeneratedValue
    val id: Long? = null,

    // Explicit snake_case mapping to match the DB schema created in SurveySubmissionSchemaInitializer.
    @field:MappedProperty("chat_id")
    val chatId: Long,

    val phone: String,

    @field:MappedProperty("project_name")
    val projectName: String,

    val purpose: String,

    @field:MappedProperty("created_at")
    val createdAt: Instant,
)
