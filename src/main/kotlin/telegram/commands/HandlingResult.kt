package telegram.commands

/**
 * Результат обработки входящего сообщения.
 */
import telegram.persistence.UserSession

data class HandlingResult(
    val handled: Boolean,
    val updatedSession: UserSession? = null,
)



