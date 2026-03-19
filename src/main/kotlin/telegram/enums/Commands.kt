package telegram.enums

/**
 * Команды бота (строки /start, /help, /cancel, /ping).
 */

enum class Commands(val text: String) {
    START("/start"),
    HELP("/help"),
    CANCEL("/cancel"),
    PING("/ping"),
    FORGET("/forget"),

    // Показать текущее состояние анкеты в любой момент.
    CHECK("/check"),
    STATUS("/status"),
}
