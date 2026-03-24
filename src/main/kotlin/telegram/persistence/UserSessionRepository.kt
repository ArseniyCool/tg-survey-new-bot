package telegram.persistence

/**
 * Репозиторий Micronaut Data для хранения пользовательских сессий.
 */

import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.time.Instant

@JdbcRepository(dialect = Dialect.POSTGRES)
interface UserSessionRepository : CrudRepository<UserSession, Long> {

    @Query(
        value = """
            INSERT INTO user_sessions (chat_id, state, phone, project_name, purpose, updated_at)
            VALUES (:chatId, :state, :phone, :projectName, :purpose, :updatedAt)
            ON CONFLICT (chat_id) DO UPDATE SET
                state = EXCLUDED.state,
                phone = EXCLUDED.phone,
                project_name = EXCLUDED.project_name,
                purpose = EXCLUDED.purpose,
                updated_at = EXCLUDED.updated_at
        """,
        nativeQuery = true,
    )
    fun upsert(
        chatId: Long,
        state: String?,
        phone: String?,
        projectName: String?,
        purpose: String?,
        updatedAt: Instant,
    )
}
