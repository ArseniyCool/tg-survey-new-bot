package blacksun.services

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject

import io.mockk.*

//стандарт тестового фреймворка
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.Message

@MicronautTest(environments = ["test"], startApplication = false)
class SurveyServiceTest {

    @field:Inject
    lateinit var service: SurveyService

    @Test
    fun `start command should return hello`() {
        val update = mockk<Update>() //фейк апдейт
        val message = mockk<Message>() //фейк сообщение
        every { update.message} returns message
        every { message.text } returns "/start"
        every { message.chatId } returns 1L //число Long

        val response = service.handle(update)

        assertEquals("Добро пожаловать!", response.text)
    }
    @Test
    fun `ping command should return pong`() {
        val update = mockk<Update>()
        val message = mockk<Message>()
        every { update.message} returns message
        every { message.text } returns "/ping"
        every { message.chatId } returns 1L

        val response = service.handle(update)

        assertEquals("Pong!", response.text)
    }
}