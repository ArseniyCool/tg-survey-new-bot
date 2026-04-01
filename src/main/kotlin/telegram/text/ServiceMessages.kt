package telegram.text

/**
 * Служебные сообщения приложения.
 */
object ServiceMessages {
    const val EMPTY_TOKEN_SKIP_COMMANDS_REGISTRATION_LOG: String =
        "telegram.token пустой; регистрацию меню команд пропускаем"

    const val COMMANDS_REGISTRATION_SUCCESS_LOG: String =
        "Меню команд Telegram успешно зарегистрировано"

    const val COMMANDS_REGISTRATION_FAILED_LOG: String =
        "Не удалось зарегистрировать меню команд Telegram: status={} body={}"

    const val COMMANDS_REGISTRATION_EXCEPTION_LOG: String =
        "Не удалось зарегистрировать меню команд Telegram (игнорируем)"

    const val SECRET_MISCONFIGURED_LOG: String =
        "Webhook secret token не настроен: задайте TELEGRAM_WEBHOOK_SECRET перед запуском приложения"

    const val INVALID_SECRET_LOG: String =
        "Отклонен запрос к webhook с невалидным secret token. remote-header-present={} path-secret-present={}"

    const val DUPLICATE_UPDATE_LOG: String =
        "Повторный update_id={} проигнорирован"

    const val PROCESSING_FAILED_LOG: String =
        "Ошибка при обработке входящего webhook-обновления от Telegram"

    const val PROCESSING_FAILED_REPLY: String =
        "⚠️ Произошла ошибка. Попробуйте позже."
}
