package telegram.services

/**
 * Тесты поведения команды /cancel (шаг назад).
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import telegram.enums.Commands
import telegram.enums.Examples
import telegram.enums.UserStates

class SurveyServiceCancelTest : SurveyServiceTestBase() {

    @Test
    fun `cancel from project step should go back to phone`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val cancelResponse = service.handle(mockTelegramUpdate(Commands.CANCEL.text))
        val txt = cancelResponse.text ?: ""
        assert(txt.contains("телефон"))

        assertEquals(UserStates.WAITING_FOR_PHONE, service.userStates[1L])
        val draft = service.drafts[1L]
        assertNotNull(draft)
        assertEquals(null, draft!!.phone)
    }

    @Test
    fun `cancel from purpose step should go back to project`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))

        val cancelResponse = service.handle(mockTelegramUpdate(Commands.CANCEL.text))
        val txt = cancelResponse.text ?: ""
        assert(txt.contains("название проекта"))

        assertEquals(UserStates.WAITING_FOR_PROJECT_NAME, service.userStates[1L])
        val draft = service.drafts[1L]
        assertNotNull(draft)
        assertEquals(null, draft!!.projectName)
        assertEquals(Examples.CORRECT_NUMBER.text, draft.phone)
    }

    @Test
    fun `cancel after completed should allow changing purpose`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        service.handle(mockTelegramUpdate(Examples.PURPOSE.text))

        assertEquals(UserStates.COMPLETED, service.userStates[1L])

        val cancelResponse = service.handle(mockTelegramUpdate(Commands.CANCEL.text))
        val txt = cancelResponse.text ?: ""
        assert(txt.contains("назначение"))

        assertEquals(UserStates.WAITING_FOR_PURPOSE, service.userStates[1L])
        val draft = service.drafts[1L]
        assertNotNull(draft)
        assertEquals(null, draft!!.purpose)
    }
}

