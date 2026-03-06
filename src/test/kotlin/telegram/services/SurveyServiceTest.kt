package telegram.services

import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.Examples
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject

import io.mockk.*

//стандарт тестового фреймворка
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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

    @BeforeEach
    fun clearState() {
        service.userStates.clear()
    }

    private fun mockTelegramUpdate(text: String): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { message.text } returns text
        every { message.chatId } returns 1L
        every { update.message } returns message

        return update
    }

    @Test
    fun `start command should return hello`() {
        val startResponse = service.handle(mockTelegramUpdate(Commands.START.text))
        assertEquals(Answers.WELCOME.text, startResponse.text)
    }
    @Test
    fun `correct phone number should return number saved`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        assertEquals(Answers.NUMBER_SAVED.text, phoneResponse.text)
    }
    @Test
    fun `incorrect phone number should return number not saved`() {
        service.handle(mockTelegramUpdate(Commands.START.text))

        val phoneResponse = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.INCORRECT_NUMBER.text, phoneResponse.text)
    }

    @Test
    fun `project command should return project saved`() {
        service.handle(mockTelegramUpdate(Commands.START.text))
        service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))

        val projectResponse = service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        assertEquals(Answers.PROJECT_SAVED.text, projectResponse.text)
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
    fun `full user survey flow should work`() {

        // 1. Start conversation
        val startResponse = service.handle(mockTelegramUpdate(Commands.START.text))
        assertEquals(Answers.WELCOME.text, startResponse.text)

        // 2. Send valid phone number
        val phoneResponse = service.handle(mockTelegramUpdate(Examples.CORRECT_NUMBER.text))
        assertEquals(Answers.NUMBER_SAVED.text, phoneResponse.text)

        // 3. Send project name
        val projectResponse = service.handle(mockTelegramUpdate(Examples.PROJECT.text))
        assertEquals(Answers.PROJECT_SAVED.text, projectResponse.text)

        // 4. After survey completion state should be cleared
        val fallbackResponse = service.handle(mockTelegramUpdate(Examples.SOMETHING.text))
        assertEquals(Answers.DONT_UNDERSTAND.text, fallbackResponse.text)
    }
}