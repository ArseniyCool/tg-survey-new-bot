package telegram.enums

/**
 * Состояния диалога: какой шаг опроса ожидается от пользователя.
 */

enum class UserStates {
    WAITING_FOR_PHONE,
    WAITING_FOR_PROJECT_NAME,
    WAITING_FOR_PURPOSE,
    COMPLETED,
}
