package telegram.commands.registration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BotCommandsCatalogTest {

    private val catalog = BotCommandsCatalog()

    @Test
    fun `should expose telegram command names without slash`() {
        val commands = catalog.definitions().map { it.command }

        assertEquals(listOf("start", "help", "privacy", "cancel", "check", "forget", "ping"), commands)
    }

    @Test
    fun `should build setMyCommands payload with registered descriptions`() {
        val payload = catalog.setMyCommandsPayload()

        assertTrue(payload.contains("\"commands\""))
        assertTrue(payload.contains("\"command\": \"start\""))
        assertTrue(payload.contains("\"description\": \"РќР°С‡Р°С‚СЊ / РїРµСЂРµР·Р°РїСѓСЃС‚РёС‚СЊ\""))
        assertTrue(payload.contains("\"command\": \"forget\""))
        assertTrue(payload.contains("\"description\": \"РЈРґР°Р»РёС‚СЊ РјРѕРё РґР°РЅРЅС‹Рµ\""))
    }
}


