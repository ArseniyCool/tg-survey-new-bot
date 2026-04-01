package telegram.webhook

/**
 * Решение о допуске входящего webhook-запроса к обработке.
 */
enum class AccessDecision {
    ALLOWED,
    MISCONFIGURED,
    DENIED,
}


