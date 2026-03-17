package telegram.enums

enum class InputLimit(
    val min: Int,
    val max: Int,
) {
    PROJECT_NAME(5, 30),
    PURPOSE(5, 100),
}
