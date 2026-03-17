package telegram.validation

/**
 * Возвращает true, если строка содержит символы, похожие на эмодзи.
 *
 * Это практичная эвристика для ввода в Telegram: она блокирует большинство популярных эмодзи,
 * флаги и emoji-последовательности (variation selectors / ZWJ).
 */
fun containsEmoji(input: String): Boolean {
    var i = 0
    while (i < input.length) {
        val cp = input.codePointAt(i)
        if (isEmojiCodePoint(cp)) return true
        i += Character.charCount(cp)
    }
    return false
}

private fun isEmojiCodePoint(cp: Int): Boolean {
    // Служебные символы для emoji-последовательностей
    if (cp == 0xFE0F) return true // VARIATION SELECTOR-16 (вариационный селектор-16)
    if (cp == 0x200D) return true // ZERO WIDTH JOINER (соединитель нулевой ширины)

    // Флаги
    if (cp in 0x1F1E6..0x1F1FF) return true

    // Основные диапазоны эмодзи (большинство современных эмодзи находится здесь)
    if (cp in 0x1F300..0x1FAFF) return true

    // Разные символы + Dingbats (многие из них Telegram трактует как эмодзи)
    if (cp in 0x2600..0x26FF) return true
    if (cp in 0x2700..0x27BF) return true

    // Технические символы (включает эмодзи вроде ⌚)
    if (cp in 0x2300..0x23FF) return true

    return false
}
