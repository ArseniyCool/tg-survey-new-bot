package telegram.services

/**
 * Тесты шага телефона: ввод текстом, ввод через контакт, валидация.
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples

class SurveyServicePhoneTest : SurveyServiceTestBase() {

    @Test
    fun `correct phone number should return number saved`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        val txt = phoneResponse.text ?: ""
        assert(txt.contains("телефон"))
        assert(txt.contains(Examples.CORRECT_NUMBER.text))
        assert(txt.contains("<code>"))
        assert(txt.contains("название проекта"))
    }

    @Test
    fun `phone can be shared as contact`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramContactUpdate("+7 (917) 396-79-03"))
        val txt = phoneResponse.text ?: ""
        assert(txt.contains("89173967903"))
        assert(txt.contains("<code>"))

        val session = sessionsStore[1L]
        assertNotNull(session)
        assertEquals("89173967903", session!!.phone)
    }

    @Test
    fun `incorrect phone number should return number not saved`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.INCORRECT_NUMBER.text, phoneResponse.text)
    }
}

