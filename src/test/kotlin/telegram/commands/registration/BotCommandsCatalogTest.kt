package telegram.commands.registration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BotCommandsCatalogTest {

    private val catalog = BotCommandsCatalog()

    @Test
    fun `should expose telegram command names without slash`() {
        val commands = catalog.commands()

        assertEquals(listOf("start", "help", "privacy", "cancel", "check", "forget", "ping"), commands)
    }

    @Test
    fun `should build setMyCommands payload with registered descriptions`() {
        val payload = catalog.setCommandsPayload()

        assertEquals(
            """{"commands":[{"command":"start","description":"Начать / перезапустить"},{"command":"help","description":"Показать справку"},{"command":"privacy","description":"Как храним данные"},{"command":"cancel","description":"Шаг назад"},{"command":"check","description":"Состояние анкеты"},{"command":"forget","description":"Удалить мои данные"},{"command":"ping","description":"Проверка связи"}]}""",
            payload
        )
        assertTrue(payload.contains(""""command":"forget""""))
    }
}
