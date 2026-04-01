package telegram.commands.registration

import jakarta.inject.Singleton
import telegram.enums.Commands

/**
 * РљР°С‚Р°Р»РѕРі РєРѕРјР°РЅРґ РґР»СЏ РјРµРЅСЋ Р±РѕС‚Р°.
 */
@Singleton
class BotCommandsCatalog {

    data class CommandDefinition(
        val command: String,
        val description: String,
    )

    fun definitions(): List<CommandDefinition> = listOf(
        CommandDefinition(Commands.START.text.removePrefix("/"), "РќР°С‡Р°С‚СЊ / РїРµСЂРµР·Р°РїСѓСЃС‚РёС‚СЊ"),
        CommandDefinition(Commands.HELP.text.removePrefix("/"), "РџРѕРєР°Р·Р°С‚СЊ СЃРїСЂР°РІРєСѓ"),
        CommandDefinition(Commands.PRIVACY.text.removePrefix("/"), "РљР°Рє С…СЂР°РЅРёРј РґР°РЅРЅС‹Рµ"),
        CommandDefinition(Commands.CANCEL.text.removePrefix("/"), "РЁР°Рі РЅР°Р·Р°Рґ"),
        CommandDefinition(Commands.CHECK.text.removePrefix("/"), "РЎРѕСЃС‚РѕСЏРЅРёРµ Р°РЅРєРµС‚С‹"),
        CommandDefinition(Commands.FORGET.text.removePrefix("/"), "РЈРґР°Р»РёС‚СЊ РјРѕРё РґР°РЅРЅС‹Рµ"),
        CommandDefinition(Commands.PING.text.removePrefix("/"), "РџСЂРѕРІРµСЂРєР° СЃРІСЏР·Рё"),
    )

    fun setMyCommandsPayload(): String {
        val commandsJson = definitions().joinToString(",\n") { definition ->
            """    {"command": "${definition.command}", "description": "${definition.description}"}"""
        }

        return "{\n  \"commands\": [\n$commandsJson\n  ]\n}"
    }
}


