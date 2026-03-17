package telegram.validation

/**
 * Accept common RU phone formats:
 * - 8XXXXXXXXXX
 * - +7XXXXXXXXXX
 * - 7XXXXXXXXXX
 * - formatted strings like "+7 (999) 123-45-67" (we strip non-digits)
 *
 * Canonical form we store/use: 8XXXXXXXXXX
 */
fun normalizePhoneNumber(phoneNumber: String): String? {
    val digits = phoneNumber.filter { it.isDigit() }

    if (digits.length == 11) {
        return when (digits[0]) {
            '8' -> digits
            '7' -> "8" + digits.substring(1)
            else -> null
        }
    }

    if (digits.length == 10) {
        // If user shared without prefix, assume local RU and add 8.
        return "8" + digits
    }

    return null
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return normalizePhoneNumber(phoneNumber) != null
}
