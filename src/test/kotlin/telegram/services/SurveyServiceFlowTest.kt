package telegram.services

/**
 * Тесты “сквозного” сценария опроса: переходы по шагам и итоговое сообщение.
 */

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples
import telegram.enums.UserStates

class SurveyServiceFlowTest : SurveyServiceTestBase() {

    @Test
    fun `project name should ask for purpose`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val projectResponse = service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        val txt = projectResponse.text ?: ""
        assert(txt.contains(Examples.PROJECT.text))
        assert(txt.contains("<code>"))
        assert(txt.contains("назначение"))
    }

    @Test
    fun `purpose should finish survey and persist submission`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))

        val purposeResponse = service.handle(mockTelegramUpdate(Examples.PURPOSE.text))

        val txt = purposeResponse.text ?: ""
        assert(txt.contains("Спасибо за заполнение анкеты"))
        assert(txt.contains("Телефон:"))
        assert(txt.contains(Examples.CORRECT_NUMBER.text))
        assert(txt.contains("Проект:"))
        assert(txt.contains(Examples.PROJECT.text))
        assert(txt.contains("Назначение:"))
        assert(txt.contains(Examples.PURPOSE.text))
        assert(txt.contains("<code>"))
        assert(txt.contains("/cancel"))
        assert(txt.contains("/start"))

        assertEquals(UserStates.COMPLETED, service.userStates[1L])

        val saved = lastSaved
        assertNotNull(saved)
        assertEquals(1L, saved!!.first)
        assertEquals(Examples.CORRECT_NUMBER.text, saved.second.phone)
        assertEquals(Examples.PROJECT.text, saved.second.projectName)
        assertEquals(Examples.PURPOSE.text, saved.second.purpose)
    }

    @Test
    fun `full user survey flow should work`() {
        val startResponse = service.handle(mockTelegramUpdate(Commands.START.text))
        assertEquals(Answers.WELCOME.text, startResponse.text)

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        assert((phoneResponse.text ?: "").contains(Examples.CORRECT_NUMBER.text))

        val projectResponse = service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        assert((projectResponse.text ?: "").contains(Examples.PROJECT.text))

        val purposeResponse = service.handle(mockTelegramUpdate(Examples.PURPOSE.text))
        assert((purposeResponse.text ?: "").contains("Спасибо за заполнение анкеты"))

        val fallbackResponse = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.DONT_UNDERSTAND.text, fallbackResponse.text)
    }
}
