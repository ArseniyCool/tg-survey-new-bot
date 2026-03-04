package blacksun.services

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject

import io.mockk.*

//стандарт тестового фреймворка
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import org.telegram.telegrambots.meta.api.objects.Update

@MicronautTest(environments = ["test"], startApplication = false)
class SurveyServiceTest {

    @field:Inject
    lateinit var service: SurveyService

    @Test
    fun `start command should return hello`() {

        val update = mockk<Update>()

        every {
            update.message.text
        } returns "/start"

        val response = service.handle(update)

        assertEquals("Hello!", response.text)
    }
}
