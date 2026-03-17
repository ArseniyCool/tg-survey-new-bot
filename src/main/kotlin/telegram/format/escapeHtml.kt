package telegram.format

/**
 * Минимальное экранирование HTML для Telegram (ParseMode.HTML).
 */
fun escapeHtml(input: String): String {
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}
