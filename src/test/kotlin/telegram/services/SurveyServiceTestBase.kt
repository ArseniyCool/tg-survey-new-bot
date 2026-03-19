package telegram.services

/**
 * Общая база для тестов SurveyService.
 *
 * Здесь лежит настройка сервиса и хелперы для сборки Update/Message, чтобы тесты были короче и читабельнее.
 */

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.telegram.telegrambots.meta.api.objects.Contact
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.persistence.SurveySubmission
import telegram.persistence.SurveySubmissionRepository
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository
import java.util.Optional

abstract class SurveyServiceTestBase {

    protected lateinit var service: SurveyService
    protected lateinit var submissions: SurveySubmissionRepository
    protected lateinit var sessions: UserSessionRepository

    protected val sessionsStore: MutableMap<Long, UserSession> = mutableMapOf()

    protected var lastSavedSubmission: SurveySubmission? = null

    @BeforeEach
    fun setUp() {
        sessionsStore.clear()
        lastSavedSubmission = null

        submissions = mockk()
        sessions = mockk()

        val savedSlot = slot<SurveySubmission>()
        every { submissions.save(capture(savedSlot)) } answers {
            lastSavedSubmission = savedSlot.captured
            savedSlot.captured
        }

        every { sessions.findById(any()) } answers {
            val id = firstArg<Long>()
            Optional.ofNullable(sessionsStore[id])
        }
        every { sessions.save(any()) } answers {
            val session = firstArg<UserSession>()
            sessionsStore[session.chatId] = session
            session
        }

        service = SurveyService(submissions, sessions)
    }

    protected fun mockTelegramUpdate(text: String, chatId: Long = 1L): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { message.text } returns text
        every { message.contact } returns null
        every { message.chatId } returns chatId
        every { update.message } returns message

        return update
    }

    protected fun mockTelegramContactUpdate(phone: String, chatId: Long = 1L): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()
        val contact = mockk<Contact>()

        every { contact.phoneNumber } returns phone
        every { message.text } returns null
        every { message.contact } returns contact
        every { message.chatId } returns chatId
        every { update.message } returns message

        return update
    }
}
