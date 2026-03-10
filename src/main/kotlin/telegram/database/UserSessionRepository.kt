package telegram.database

// CODEX: Unify packages to telegram.* (repository under telegram.database).
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.repository.CrudRepository
import io.micronaut.data.model.query.builder.sql.Dialect

import telegram.entity.UserSession
@JdbcRepository(dialect = Dialect.POSTGRES)
interface UserSessionRepository : CrudRepository<UserSession, Long>