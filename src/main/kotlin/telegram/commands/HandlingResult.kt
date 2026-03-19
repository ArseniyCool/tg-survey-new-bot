package telegram.commands

/**
 * Результат обработки входящего сообщения:
 * - handled: было ли сообщение обработано
 * - updatedSession: нужно ли сохранить обновленную сессию пользователя в БД
 */
import telegram.persistence.UserSession

data class HandlingResult(
    val handled: Boolean,
    val updatedSession: UserSession? = null,
)

