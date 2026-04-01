package telegram.webhook

import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class ResponderTest {

    private val responder = Responder()

    @Test
    fun `toHttpResponse should return forbidden status when result is forbidden`() {
        val response = responder.toHttpResponse(ProcessingResult.forbidden())

        assertEquals(HttpStatus.FORBIDDEN, response.status)
    }

    @Test
    fun `toHttpResponse should return empty 200 when chat id is missing`() {
        val response = responder.toHttpResponse(ProcessingResult.reply(null, "hello"))

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(null, response.body())
    }

    @Test
    fun `toHttpResponse should return empty 200 when text is blank`() {
        val response = responder.toHttpResponse(ProcessingResult.reply(1L, "   "))

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(null, response.body())
    }

    @Test
    fun `toHttpResponse should build send message with html parse mode`() {
        val response = responder.toHttpResponse(ProcessingResult.reply(5L, "<b>Hello</b>"))
        val body = response.body()

        assertTrue(body is SendMessage)
        val sendMessage = body as SendMessage
        assertEquals("5", sendMessage.chatId)
        assertEquals("<b>Hello</b>", sendMessage.text)
        assertEquals(ParseMode.HTML, sendMessage.parseMode)
    }
}


