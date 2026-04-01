package telegram.commands.registration

import jakarta.inject.Singleton
import telegram.enums.Commands

/**
 * Каталог команд для меню бота.
 */
@Singleton
class BotCommandsCatalog {

    data class CommandDefinition(
        val command: String,
        val description: String,
    )

    fun definitions(): List<CommandDefinition> = listOf(
        CommandDefinition(Commands.START.text.removePrefix("/"), "Начать / перезапустить"),
        CommandDefinition(Commands.HELP.text.removePrefix("/"), "Показать справку"),
        CommandDefinition(Commands.PRIVACY.text.removePrefix("/"), "Как храним данные"),
        CommandDefinition(Commands.CANCEL.text.removePrefix("/"), "Шаг назад"),
        CommandDefinition(Commands.CHECK.text.removePrefix("/"), "Состояние анкеты"),
        CommandDefinition(Commands.FORGET.text.removePrefix("/"), "Удалить мои данные"),
        CommandDefinition(Commands.PING.text.removePrefix("/"), "Проверка связи"),
    )

    fun setMyCommandsPayload(): String {
        val commandsJson = definitions().joinToString(",\n") { definition ->
            """    {"command": "${definition.command}", "description": "${definition.description}"}"""
        }

        return "{\n  \"commands\": [\n$commandsJson\n  ]\n}"
    }
}
