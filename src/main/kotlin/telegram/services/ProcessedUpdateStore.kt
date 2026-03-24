package telegram.services

/**
 * Защита от повторной обработки одинаковых Telegram update_id.
 *
 * Если один и тот же webhook приходит повторно, мы не запускаем бизнес-логику второй раз.
 */

import io.micronaut.data.connection.annotation.Connectable
import jakarta.inject.Singleton
import java.sql.Timestamp
import java.time.Instant
import javax.sql.DataSource

@Singleton
open class ProcessedUpdateStore(
    private val dataSource: DataSource,
) {
    @Connectable
    open fun tryAcquire(updateId: Long): Boolean {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                INSERT INTO processed_updates (update_id, status, updated_at)
                VALUES (?, ?, ?)
                ON CONFLICT (update_id) DO NOTHING
                """.trimIndent()
            ).use { statement ->
                statement.setLong(1, updateId)
                statement.setString(2, "PROCESSING")
                statement.setTimestamp(3, Timestamp.from(Instant.now()))
                return statement.executeUpdate() == 1
            }
        }
    }

    @Connectable
    open fun markCompleted(updateId: Long) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                UPDATE processed_updates
                SET status = ?, updated_at = ?
                WHERE update_id = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, "COMPLETED")
                statement.setTimestamp(2, Timestamp.from(Instant.now()))
                statement.setLong(3, updateId)
                statement.executeUpdate()
            }
        }
    }

    @Connectable
    open fun release(updateId: Long) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                DELETE FROM processed_updates
                WHERE update_id = ? AND status = ?
                """.trimIndent()
            ).use { statement ->
                statement.setLong(1, updateId)
                statement.setString(2, "PROCESSING")
                statement.executeUpdate()
            }
        }
    }
}
