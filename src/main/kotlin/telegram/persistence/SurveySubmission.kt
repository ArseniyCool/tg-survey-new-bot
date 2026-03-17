package telegram.persistence

/**
 * Сущность анкеты для таблицы survey_submissions (Micronaut Data).
 */

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

    // Явное snake_case-сопоставление, чтобы совпадать со схемой БД, которую создает SurveySubmissionSchemaInitializer.
    @field:MappedProperty("chat_id")
    val chatId: Long,

    val phone: String,

    @field:MappedProperty("project_name")
    val projectName: String,

    val purpose: String,

    @field:MappedProperty("created_at")
    val createdAt: Instant,
)
