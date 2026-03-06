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
    //lateinit - инициализируется позже
    //Kotlin имеет: property field getter setter
    //Micronaut работает именно с field.
    //Inject ("вставить") автоматически подставить объект.

    private fun mockUpdate(text: String): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { message.text } returns text
        every { message.chatId } returns 1L
        every { update.message } returns message

        return update
    }

    @Test
    fun `start command should return hello`() {
        val startResponse = service.handle(mockUpdate("/start"))
        assertEquals(Answers.WELCOME.text, startResponse.text)
    }
    @Test
    fun `correct phone number should return number saved`() {
        service.handle(mockUpdate("/start"))

        val phoneResponse = service.handle(mockUpdate("82143965701"))
        assertEquals(Answers.NUMBERSAVED.text, phoneResponse.text)
    }
    @Test
    fun `incorrect phone number should return number saved`() {
        service.handle(mockUpdate("/start"))

        val phoneResponse = service.handle(mockUpdate("-72143965701"))
        assertEquals(Answers.INCORRECTNUMBER.text, phoneResponse.text)
    }

    @Test
    fun `project command should return project saved`() {
        service.handle(mockUpdate("/start"))
        service.handle(mockUpdate("82143965701"))

        val projectResponse = service.handle(mockUpdate("My Project"))
        assertEquals(Answers.PROJECTSAVED.text, projectResponse.text)
    }

    @Test
    fun `caps command should go through too`() {
        val response = service.handle(mockUpdate("/StArT"))
        assertEquals(Answers.WELCOME.text, response.text)
    }

    @Test
    fun `ping command should return pong`() {
        val response = service.handle(mockUpdate("/ping"))
        assertEquals(Answers.PONG.text, response.text)
    }

    @Test
    fun `ping command should return pong even state active`() {
        service.handle(mockUpdate("/start"))

        val response = service.handle(mockUpdate("/ping"))
        assertEquals(Answers.PONG.text, response.text)
    }

    @Test
    fun `not find command should return not found`() {
        val response = service.handle(mockUpdate("something"))
        assertEquals(Answers.DONTUNDERSTAND.text, response.text)
    }
}