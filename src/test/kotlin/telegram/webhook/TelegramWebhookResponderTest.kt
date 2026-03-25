package telegram.webhook

import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class TelegramWebhookResponderTest {

    private val responder = TelegramWebhookResponder()

    @Test
    fun `ok should return empty 200 when chat id is missing`() {
        val response = responder.ok(null, "hello")

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(null, response.body())
    }

    @Test
    fun `ok should return empty 200 when text is blank`() {
        val response = responder.ok(1L, "   ")

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(null, response.body())
    }

    @Test
    fun `ok should build send message with html parse mode`() {
        val response = responder.ok(5L, "<b>Hello</b>")
        val body = response.body()

        assertTrue(body is SendMessage)
        val sendMessage = body as SendMessage
        assertEquals("5", sendMessage.chatId)
        assertEquals("<b>Hello</b>", sendMessage.text)
        assertEquals(ParseMode.HTML, sendMessage.parseMode)
    }
}
