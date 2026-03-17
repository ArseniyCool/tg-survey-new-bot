package telegram.enums

enum class Commands(val text: String) {
    START("/start"),
    HELP("/help"),
    CANCEL("/cancel"),
    PING("/ping"),
}
