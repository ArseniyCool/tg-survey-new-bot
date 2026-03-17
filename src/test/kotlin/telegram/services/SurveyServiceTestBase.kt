package telegram.services

/**
 * Общая база для тестов SurveyService.
 *
 * Здесь лежит настройка сервиса и хелперы для сборки Update/Message, чтобы тесты были короче и читабельнее.
 */

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.telegram.telegrambots.meta.api.objects.Contact
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.model.SurveyDraft
import telegram.persistence.SurveySubmissionWriter

abstract class SurveyServiceTestBase {

    protected lateinit var service: SurveyService
    protected var lastSaved: Pair<Long, SurveyDraft>? = null

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

