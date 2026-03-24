package telegram.commands

/**
 * Тесты глобальных команд (/start, /ping, /forget, /check) и базового fallback-поведения.
 */

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples
import telegram.services.SurveyServiceTestBase

class SurveyCommandsTest : SurveyServiceTestBase() {

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
    fun `unknown plain text should return fallback`() {
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
        service.handle(mockTelegramUpdate(Commands.START.text))
        assertNotNull(sessionsStore[1L])

        val response = service.handle(mockTelegramUpdate(Commands.FORGET.text))
        val txt = response.text ?: ""
        assertTrue(txt.contains("данные удалены"))
        assertTrue(txt.contains("/start"))

        assertNull(sessionsStore[1L])
    }

    @Test
    fun `unknown slash command should return unknown-command message`() {
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

        assertNull(sessionsStore[1L])
    }

    @Test
    fun `status command should act as alias for check`() {
        val response = service.handle(mockTelegramUpdate(Commands.STATUS.text))
        val txt = response.text ?: ""
        assertTrue(txt.contains("Состояние анкеты") || txt.contains("Анкета еще не начата"))
    }
}
