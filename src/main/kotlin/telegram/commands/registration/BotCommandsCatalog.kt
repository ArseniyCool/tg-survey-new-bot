package telegram.commands.registration

import jakarta.inject.Singleton
import telegram.enums.Commands

/**
 * Каталог команд для меню бота.
 */
@Singleton
class BotCommandsCatalog {
    private val commandDescriptions = listOf(
        Commands.START.telegramName to "Начать / перезапустить",
        Commands.HELP.telegramName to "Показать справку",
        Commands.PRIVACY.telegramName to "Как храним данные",
        Commands.CANCEL.telegramName to "Шаг назад",
        Commands.CHECK.telegramName to "Состояние анкеты",
        Commands.FORGET.telegramName to "Удалить мои данные",
        Commands.PING.telegramName to "Проверка связи",
    )

    fun commands(): List<String> = commandDescriptions.map { it.first }

    fun setCommandsPayload(): String {
        val commandsJson = commandDescriptions.joinToString(",") { (command, description) ->
            """{"command":"$command","description":"$description"}"""
        }

        return """{"commands":[$commandsJson]}"""
    }
}
