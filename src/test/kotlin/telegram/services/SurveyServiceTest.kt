package telegram.services

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Contact
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples
import telegram.enums.UserStates
import telegram.model.SurveyDraft
import telegram.persistence.SurveySubmissionWriter

class SurveyServiceTest {

    private lateinit var service: SurveyService
    private var lastSaved: Pair<Long, SurveyDraft>? = null

    @BeforeEach
    fun setUp() {
        lastSaved = null
        val writer = object : SurveySubmissionWriter {
            override fun write(chatId: Long, draft: SurveyDraft) {
                lastSaved = chatId to draft
            }
        }

        service = SurveyService(writer)
        service.userStates.clear()
        service.drafts.clear()
    }

    private fun mockTelegramUpdate(text: String): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { message.text } returns text
        every { message.contact } returns null
        every { message.chatId } returns 1L
        every { update.message } returns message

        return update
    }

    private fun mockTelegramContactUpdate(phone: String): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()
        val contact = mockk<Contact>()

        every { contact.phoneNumber } returns phone
        every { message.text } returns null
        every { message.contact } returns contact
        every { message.chatId } returns 1L
        every { update.message } returns message

        return update
    }

    @Test
    fun `start command should return hello`() {
        val startResponse = service.handle(mockTelegramUpdate(Commands.START.text))
        assertEquals(Answers.WELCOME.text, startResponse.text)
        assertNotNull(startResponse.replyMarkup)
    }

    @Test
    fun `correct phone number should return number saved`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        assertEquals(Answers.NUMBER_SAVED.text, phoneResponse.text)
    }

    @Test
    fun `phone can be shared as contact`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramContactUpdate("+7 (917) 396-79-03"))
        assertEquals(Answers.NUMBER_SAVED.text, phoneResponse.text)

        val draft = service.drafts[1L]
        assertNotNull(draft)
        assertEquals("89173967903", draft!!.phone)
    }

    @Test
    fun `incorrect phone number should return number not saved`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.INCORRECT_NUMBER.text, phoneResponse.text)
    }

    @Test
    fun `project name should ask for purpose`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val projectResponse = service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        assertEquals(Answers.PROJECT_SAVED.text, projectResponse.text)
    }

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

    @Test
    fun `purpose should finish survey and persist submission`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        service.handle(mockTelegramUpdate(Examples.PROJECT.text))

        val purposeResponse = service.handle(mockTelegramUpdate(Examples.PURPOSE.text))

        val txt = purposeResponse.text ?: ""
        // Summary should include saved fields and commands
        assert(txt.contains("Телефон:"))
        assert(txt.contains(Examples.CORRECT_NUMBER.text))
        assert(txt.contains("Проект:"))
        assert(txt.contains(Examples.PROJECT.text))
        assert(txt.contains("Назначение:"))
        assert(txt.contains(Examples.PURPOSE.text))
        assert(txt.contains("/cancel"))
        assert(txt.contains("/start"))

        val saved = lastSaved
        assertNotNull(saved)
        assertEquals(1L, saved!!.first)
        assertEquals(Examples.CORRECT_NUMBER.text, saved.second.phone)
        assertEquals(Examples.PROJECT.text, saved.second.projectName)
        assertEquals(Examples.PURPOSE.text, saved.second.purpose)
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
    fun `full user survey flow should work`() {
        val startResponse = service.handle(mockTelegramUpdate(Commands.START.text))
        assertEquals(Answers.WELCOME.text, startResponse.text)

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        assertEquals(Answers.NUMBER_SAVED.text, phoneResponse.text)

        val projectResponse = service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        assertEquals(Answers.PROJECT_SAVED.text, projectResponse.text)

        val purposeResponse = service.handle(mockTelegramUpdate(Examples.PURPOSE.text))

        val txt = purposeResponse.text ?: ""
        // Summary should include saved fields and commands
        assert(txt.contains("Телефон:"))
        assert(txt.contains(Examples.CORRECT_NUMBER.text))
        assert(txt.contains("Проект:"))
        assert(txt.contains(Examples.PROJECT.text))
        assert(txt.contains("Назначение:"))
        assert(txt.contains(Examples.PURPOSE.text))
        assert(txt.contains("/cancel"))

        val fallbackResponse = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.DONT_UNDERSTAND.text, fallbackResponse.text)
    }
}











