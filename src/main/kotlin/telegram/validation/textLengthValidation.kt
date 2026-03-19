package telegram.validation

/**
 * Проверка длины пользовательского ввода (min/max).
 */
fun isLengthInRange(value: String, min: Int, max: Int): Boolean {
    val len = value.length
    return len in min..max
}

