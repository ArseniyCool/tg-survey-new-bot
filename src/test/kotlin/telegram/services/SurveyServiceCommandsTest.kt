package telegram.services

/**
 * Тесты глобальных команд (/start, /ping) и базового fallback-поведения.
 */

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

class SurveyServiceCommandsTest : SurveyServiceTestBase() {

    @Test
    fun `start command should return hello`() {
        val startResponse = service.handle(mockTelegramUpdate(Commands.START.text))
        assertEquals(Answers.WELCOME.text, startResponse.text)
        assertNotNull(startResponse.replyMarkup)
    }

    @Test
    fun `caps command should go through too`() {
        val response = service.handle(mockTelegramUpdate("/StArT"))
        assertEquals(Answers.WELCOME.text, response.text)
    }

    @Test
    fun `ping command should return pong`() {
        val response = service.handle(mockTelegramUpdate(Commands.PING.text))
        assertEquals(Answers.PONG.text, response.text)
    }

    @Test
    fun `ping command should return pong even state active`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val response = service.handle(mockTelegramUpdate(Commands.PING.text))
        assertEquals(Answers.PONG.text, response.text)
    }

    @Test
    fun `not find command should return not found`() {
        val response = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.DONT_UNDERSTAND.text, response.text)
    }

    @Test
    fun `non-text update should return fallback`() {
        val update = mockk<Update>()
        every { update.message } returns null

        val response = service.handle(update)
        assertEquals(Answers.DONT_UNDERSTAND.text, response.text)
    }

    @Test
    fun `forget command should delete user data`() {
        // создадим "сессию" в нашем тестовом хранилище, чтобы было что удалять
        service.handle(mockTelegramUpdate(Commands.START.text))
        assertNotNull(sessionsStore[1L])

        val response = service.handle(mockTelegramUpdate(Commands.FORGET.text))
        val txt = response.text ?: ""
        assert(txt.contains("данные удалены"))
        assert(txt.contains("/start"))

        // сессия должна быть удалена из БД (у нас это имитируется sessionsStore)
        assertNull(sessionsStore[1L])
    }

    @Test
    fun `unknown slash command should return unknown-command message`() {
        // Любое сообщение, начинающееся с '/', трактуем как команду.
        val response = service.handle(mockTelegramUpdate("/abracadabra"))
        val txt = response.text ?: ""
        assertTrue(txt.contains("Команда"))
        assertTrue(txt.contains("/help"))
    }

    @Test
    fun `check command should show status without creating session`() {
        val response = service.handle(mockTelegramUpdate(Commands.CHECK.text))
        val txt = response.text ?: ""
        assertTrue(txt.contains("Состояние анкеты") || txt.contains("Анкета еще не начата"))

        // /check не должен создавать запись в БД, если пользователь еще не начинал опрос.
        assertNull(sessionsStore[1L])
    }

    @Test
    fun `status command should act as alias for check`() {
        val response = service.handle(mockTelegramUpdate(Commands.STATUS.text))
        val txt = response.text ?: ""
        assertTrue(txt.contains("Состояние анкеты") || txt.contains("Анкета еще не начата"))
    }
}
