package telegram.commands.state

/**
 * Тесты шага ввода телефона.
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples
import telegram.services.SurveyServiceTestBase

class StatePhoneTest : SurveyServiceTestBase() {

    @Test
    fun `correct phone number should return saved message`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val response = service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        val txt = response.text ?: ""

        assertTrue(txt.contains("телефон"))
        assertTrue(txt.contains(Examples.CORRECT_NUMBER.text))
        assertTrue(txt.contains("<code>"))
        assertTrue(txt.contains("название проекта"))
    }

    @Test
    fun `phone can be shared as contact`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val response = service.handle(mockTelegramContactUpdate("+7 (888) 888-88-88"))
        val txt = response.text ?: ""

        assertTrue(txt.contains("88888888888"))
        assertTrue(txt.contains("<code>"))

        val session = sessionsStore[1L]
        assertNotNull(session)
        assertEquals("88888888888", session!!.phone)
    }

    @Test
    fun `incorrect phone number should return validation error`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val response = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.INCORRECT_NUMBER.text, response.text)
    }
}
