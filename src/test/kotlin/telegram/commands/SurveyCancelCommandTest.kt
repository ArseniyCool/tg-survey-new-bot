package telegram.commands

/**
 * Тесты поведения команды /cancel (шаг назад).
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import telegram.enums.Commands
import telegram.enums.Examples
import telegram.enums.UserStates
import telegram.services.SurveyServiceTestBase

class SurveyCancelCommandTest : SurveyServiceTestBase() {

    @Test
    fun `cancel from project step should go back to phone`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val cancelResponse = service.handle(mockTelegramUpdate(Commands.CANCEL.text))
        val txt = cancelResponse.text ?: ""
        assertTrue(txt.contains("телефон"))

        val session = sessionsStore[1L]
        assertNotNull(session)
        assertEquals(UserStates.WAITING_FOR_PHONE, session!!.state)
        assertEquals(null, session.phone)
    }

    @Test
    fun `cancel from purpose step should go back to project`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))

        val cancelResponse = service.handle(mockTelegramUpdate(Commands.CANCEL.text))
        val txt = cancelResponse.text ?: ""
        assertTrue(txt.contains("название проекта"))

        val session = sessionsStore[1L]
        assertNotNull(session)
        assertEquals(UserStates.WAITING_FOR_PROJECT_NAME, session!!.state)
        assertEquals(null, session.projectName)
        assertEquals(Examples.CORRECT_NUMBER.text, session.phone)
    }

    @Test
    fun `cancel after completed should allow changing purpose`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        service.handle(mockTelegramUpdate(Examples.PURPOSE.text))

        assertEquals(UserStates.COMPLETED, sessionsStore[1L]?.state)

        val cancelResponse = service.handle(mockTelegramUpdate(Commands.CANCEL.text))
        val txt = cancelResponse.text ?: ""
        assertTrue(txt.contains("назначение"))

        val session = sessionsStore[1L]
        assertNotNull(session)
        assertEquals(UserStates.WAITING_FOR_PURPOSE, session!!.state)
        assertEquals(null, session.purpose)
    }
}
