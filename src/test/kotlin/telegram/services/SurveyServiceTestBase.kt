package telegram.services

/**
 * Общая база для тестов SurveyService.
 */

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.telegram.telegrambots.meta.api.objects.Contact
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository
import java.util.Optional

abstract class SurveyServiceTestBase {

    protected lateinit var service: SurveyService
    protected lateinit var sessions: UserSessionRepository
    protected lateinit var userSessionStore: UserSessionStore

    protected val sessionsStore: MutableMap<Long, UserSession> = mutableMapOf()

    @BeforeEach
    fun setUp() {
        sessionsStore.clear()
        sessions = mockk()

        every { sessions.deleteById(any()) } answers {
            val id = firstArg<Long>()
            sessionsStore.remove(id)
        }

        every { sessions.findById(any()) } answers {
            val id = firstArg<Long>()
            Optional.ofNullable(sessionsStore[id])
        }

        every {
            sessions.upsert(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } answers {
            val chatId = firstArg<Long>()
            val state = secondArg<String?>()?.let { telegram.enums.UserStates.valueOf(it) }
            val phone = arg<String?>(2)
            val projectName = arg<String?>(3)
            val purpose = arg<String?>(4)
            val updatedAt = arg<java.time.Instant>(5)

            sessionsStore[chatId] = UserSession(
                chatId = chatId,
                state = state,
                phone = phone,
                projectName = projectName,
                purpose = purpose,
                updatedAt = updatedAt,
            )
        }

        userSessionStore = UserSessionStore(sessions)
        service = SurveyService(userSessionStore)
    }

    protected fun mockTelegramUpdate(text: String, chatId: Long = 1L, updateId: Int = 1): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { message.text } returns text
        every { message.contact } returns null
        every { message.chatId } returns chatId
        every { update.message } returns message
        every { update.updateId } returns updateId

        return update
    }

    protected fun mockTelegramContactUpdate(phone: String, chatId: Long = 1L, updateId: Int = 1): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()
        val contact = mockk<Contact>()

        every { contact.phoneNumber } returns phone
        every { message.text } returns null
        every { message.contact } returns contact
        every { message.chatId } returns chatId
        every { update.message } returns message
        every { update.updateId } returns updateId

        return update
    }
}
