package telegram.validation

/**
 * Returns true if the string contains emoji-like code points.
 *
 * This is a pragmatic heuristic for Telegram input: it blocks most common emojis,
 * flags and emoji sequences (variation selectors / ZWJ).
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
    // Emoji sequences helpers
    if (cp == 0xFE0F) return true // VARIATION SELECTOR-16
    if (cp == 0x200D) return true // ZERO WIDTH JOINER

    // Flags
    if (cp in 0x1F1E6..0x1F1FF) return true

    // Main emoji blocks (most of modern emoji lives here)
    if (cp in 0x1F300..0x1FAFF) return true

    // Misc Symbols + Dingbats (many are treated as emoji in Telegram)
    if (cp in 0x2600..0x26FF) return true
    if (cp in 0x2700..0x27BF) return true

    // Misc Technical (includes emoji like ⌚)
    if (cp in 0x2300..0x23FF) return true

    return false
}
