package telegram.services

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Contact
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class IncomingTelegramMessageTest {

    @Test
    fun `parser should extract normalized command with bot suffix and args`() {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { message.chatId } returns 42L
        every { message.text } returns "/StArT@SurveyBot payload"
        every { message.contact } returns null
        every { update.message } returns message

        val parsed = parseIncomingTelegramMessage(update)

        assertEquals(42L, parsed?.chatId)
        assertEquals("/StArT@SurveyBot payload", parsed?.rawText)
        assertEquals("/start", parsed?.normalizedCommand)
    }

    @Test
    fun `parser should use contact phone when text is absent`() {
        val update = mockk<Update>()
        val message = mockk<Message>()
        val contact = mockk<Contact>()

        every { message.chatId } returns 7L
        every { message.text } returns null
        every { contact.phoneNumber } returns "+7 917 396 79 03"
        every { message.contact } returns contact
        every { update.message } returns message

        val parsed = parseIncomingTelegramMessage(update)

        assertEquals(7L, parsed?.chatId)
        assertEquals("+7 917 396 79 03", parsed?.rawText)
        assertNull(parsed?.normalizedCommand)
    }

    @Test
    fun `parser should return null for blank payload`() {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { message.chatId } returns 1L
        every { message.text } returns "   "
        every { message.contact } returns null
        every { update.message } returns message

        assertNull(parseIncomingTelegramMessage(update))
    }
}
