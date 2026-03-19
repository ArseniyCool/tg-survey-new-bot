package telegram.persistence

/**
 * Сессия пользователя, хранимая в базе данных.
 *
 * Нужна, чтобы бот не терял прогресс при перезапуске и мог работать в нескольких экземплярах.
 */

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import java.time.Instant
import telegram.enums.UserStates

@MappedEntity("user_sessions")
data class UserSession(
    @field:Id
    @field:MappedProperty("chat_id")
    val chatId: Long,

    val state: UserStates? = null,

    val phone: String? = null,

    @field:MappedProperty("project_name")
    val projectName: String? = null,

    val purpose: String? = null,

    @field:MappedProperty("updated_at")
    val updatedAt: Instant = Instant.now(),
)

