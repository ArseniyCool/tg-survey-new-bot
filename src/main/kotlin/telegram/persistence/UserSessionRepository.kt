package telegram.persistence

/**
 * Репозиторий Micronaut Data для хранения прогресса (сессии) пользователя.
 */

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface UserSessionRepository : CrudRepository<UserSession, Long>

