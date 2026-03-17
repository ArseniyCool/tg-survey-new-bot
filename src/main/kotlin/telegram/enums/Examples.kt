package telegram.enums

/**
 * Примеры входных данных для тестов (телефон, проект, назначение).
 */

enum class Examples(val text: String) {
    CORRECT_NUMBER("89173967903"),
    PROJECT("My project"),
    PURPOSE("Для автоматизации опросов"),
    SOMETHING("blablabla"),
}
