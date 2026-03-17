package telegram.services

/**
 * Тесты валидаций текстового ввода: эмодзи, длина, сохранение регистра.
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples
import telegram.enums.UserStates

class SurveyServiceValidationTest : SurveyServiceTestBase() {

    @Test
    fun `emoji in project name should be rejected`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val response = service.handle(mockTelegramUpdate("My project 🚀"))
        assertEquals(Answers.EMOJI_NOT_ALLOWED.text, response.text)
        assertEquals(UserStates.WAITING_FOR_PROJECT_NAME, service.userStates[1L])

        val draft = service.drafts[1L]
        assertNotNull(draft)
        assertEquals(null, draft!!.projectName)
    }

    @Test
    fun `emoji in purpose should be rejected`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))

        val response = service.handle(mockTelegramUpdate("Для 🚀"))
        assertEquals(Answers.EMOJI_NOT_ALLOWED.text, response.text)
        assertEquals(UserStates.WAITING_FOR_PURPOSE, service.userStates[1L])

        val draft = service.drafts[1L]
        assertNotNull(draft)
        assertEquals(null, draft!!.purpose)
    }

    @Test
    fun `project name length should be validated`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val tooShort = service.handle(mockTelegramUpdate("Abcd"))
        assertEquals(Answers.PROJECT_NAME_LENGTH_INVALID.text, tooShort.text)
        assertEquals(UserStates.WAITING_FOR_PROJECT_NAME, service.userStates[1L])

        val tooLongName = "A".repeat(31)
        val tooLong = service.handle(mockTelegramUpdate(tooLongName))
        assertEquals(Answers.PROJECT_NAME_LENGTH_INVALID.text, tooLong.text)
        assertEquals(UserStates.WAITING_FOR_PROJECT_NAME, service.userStates[1L])
    }

    @Test
    fun `purpose length should be validated`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))

        val tooShort = service.handle(mockTelegramUpdate("Abcd"))
        assertEquals(Answers.PURPOSE_LENGTH_INVALID.text, tooShort.text)
        assertEquals(UserStates.WAITING_FOR_PURPOSE, service.userStates[1L])

        val tooLongPurpose = "A".repeat(101)
        val tooLong = service.handle(mockTelegramUpdate(tooLongPurpose))
        assertEquals(Answers.PURPOSE_LENGTH_INVALID.text, tooLong.text)
        assertEquals(UserStates.WAITING_FOR_PURPOSE, service.userStates[1L])
    }

    @Test
    fun `project and purpose should preserve original casing`() {
        service.handle(mockTelegramUpdate("/StArT"))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val projectInput = "My COOL Project"
        service.handle(mockTelegramUpdate(projectInput))

        val draftAfterProject = service.drafts[1L]
        assertNotNull(draftAfterProject)
        assertEquals(projectInput, draftAfterProject!!.projectName)

        val purposeInput = "For Internal Automation"
        service.handle(mockTelegramUpdate(purposeInput))

        val draftAfterPurpose = service.drafts[1L]
        assertNotNull(draftAfterPurpose)
        assertEquals(purposeInput, draftAfterPurpose!!.purpose)
        assertEquals(UserStates.COMPLETED, service.userStates[1L])
    }
}

