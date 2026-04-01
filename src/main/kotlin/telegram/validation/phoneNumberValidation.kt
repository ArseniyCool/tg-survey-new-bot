package telegram.validation

/**
 * Нормализует номер телефона к формату 8XXXXXXXXXX.
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
        return "8" + digits
    }

    return null
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return normalizePhoneNumber(phoneNumber) != null
}

