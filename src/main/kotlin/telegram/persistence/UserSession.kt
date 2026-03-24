package telegram.persistence

/**
 * Пользовательская сессия, сохраняемая в БД.
 *
 * Нужна, чтобы бот не терял прогресс между сообщениями и перезапусками приложения.
 */

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.Instant
import telegram.enums.UserStates

@MappedEntity("user_sessions")
data class UserSession(
    @field:Id
    @field:MappedProperty("chat_id")
    val chatId: Long,

    @field:TypeDef(type = DataType.STRING)
    val state: UserStates? = null,

    val phone: String? = null,

    @field:MappedProperty("project_name")
    val projectName: String? = null,

    val purpose: String? = null,

    @field:MappedProperty("updated_at")
    val updatedAt: Instant = Instant.now(),
)
