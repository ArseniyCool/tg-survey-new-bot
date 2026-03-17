package telegram.persistence

/**
 * CRUD-репозиторий Micronaut Data для работы с таблицей анкет.
 */

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SurveySubmissionRepository : CrudRepository<SurveySubmission, Long>