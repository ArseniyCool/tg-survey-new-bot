package telegram.services

/**
 * Защита от повторной обработки одинаковых Telegram update_id.
 *
 * Если один и тот же webhook приходит повторно, мы не запускаем бизнес-логику второй раз.
 */

import io.micronaut.data.connection.annotation.Connectable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import javax.sql.DataSource

@Singleton
open class ProcessedUpdateStore(
    private val dataSource: DataSource,
) {
    private val log = LoggerFactory.getLogger(ProcessedUpdateStore::class.java)

    @Connectable
    open fun tryAcquire(updateId: Long): Boolean {
        try {
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
                    val inserted = statement.executeUpdate() == 1

                    log.info("event=update_acquire updateId={} acquired={}", updateId, inserted)
                    return inserted
                }
            }
        } catch (e: SQLException) {
            log.error(
                "event=update_acquire_failed updateId={} reason=processed_updates_unavailable",
                updateId,
                e
            )
            throw IllegalStateException(
                "Не удалось зарезервировать processed_updates. Проверьте, что миграции Flyway применены и таблица processed_updates существует.",
                e
            )
        }
    }

    @Connectable
    open fun markCompleted(updateId: Long) {
        try {
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

            log.info("event=update_completed updateId={}", updateId)
        } catch (e: SQLException) {
            log.error("event=update_completed_failed updateId={}", updateId, e)
            throw IllegalStateException("Не удалось отметить update как завершенный.", e)
        }
    }

    @Connectable
    open fun release(updateId: Long) {
        try {
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

            log.warn("event=update_released updateId={}", updateId)
        } catch (e: SQLException) {
            log.error("event=update_release_failed updateId={}", updateId, e)
            throw IllegalStateException("Не удалось освободить update после ошибки.", e)
        }
    }
}
