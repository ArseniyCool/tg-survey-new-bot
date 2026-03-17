package telegram.validation

/**
 * Принимает распространенные форматы телефонов РФ:
 * - 8XXXXXXXXXX
 * - +7XXXXXXXXXX
 * - 7XXXXXXXXXX
 * - строки с форматированием, например "+7 (999) 123-45-67" (мы убираем все, кроме цифр)
 *
 * Канонический формат, который мы храним/используем: 8XXXXXXXXXX
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
        // Если пользователь прислал 10 цифр без префикса, считаем это РФ-номером и добавляем 8.
        return "8" + digits
    }

    return null
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return normalizePhoneNumber(phoneNumber) != null
}
