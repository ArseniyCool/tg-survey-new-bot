package telegram.format

/**
 * Minimal HTML escaping for Telegram ParseMode.HTML.
 */
fun escapeHtml(input: String): String {
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}
