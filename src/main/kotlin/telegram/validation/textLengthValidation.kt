package telegram.validation

fun isLengthInRange(value: String, min: Int, max: Int): Boolean {
    val len = value.length
    return len in min..max
}
