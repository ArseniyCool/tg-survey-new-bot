package telegram.enums

/**
 * Состояния диалога: какой шаг опроса ожидается от пользователя.
 */
enum class UserStates(val stepIndex: Int) {
    WAITING_FOR_PHONE(0),
    WAITING_FOR_PROJECT_NAME(1),
    WAITING_FOR_PURPOSE(2),
    COMPLETED(3),
}

