package telegram.validation

private val phoneRegex = Regex("^(\\+7|8)\\d{10}$") // +7XXXXXXXXXX или 8XXXXXXXXXX
fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneRegex.matches(phoneNumber)
    }