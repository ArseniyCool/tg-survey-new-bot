package telegram.commands

/**
 * Результат обработки входа: была ли обработка выполнена и нужно ли сохранить обновленную сессию пользователя.
 */

import telegram.persistence.UserSession

data class HandlingResult(
    val handled: Boolean,
    val updatedSession: UserSession? = null,
)

