package telegram.webhook

import io.micronaut.http.HttpStatus
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.model.BotReply
import telegram.services.ProcessedUpdateStore
import telegram.services.SurveyService
import telegram.text.ServiceMessages

class ProcessTelegramUpdateUseCaseTest {

    private val surveyService = mockk<SurveyService>()
    private val processedUpdateStore = mockk<ProcessedUpdateStore>()
    private val useCase = ProcessTelegramUpdateUseCase(
        surveyService,
        processedUpdateStore,
    )

    @Test
    fun `should acknowledge duplicate update without calling survey service`() {
        val update = mockUpdate(100L, 42L)
        every { processedUpdateStore.tryAcquire(100L) } returns false

        val result = useCase.execute(update)

        assertEquals(HttpStatus.OK, result.status)
        assertEquals(null, result.text)
        verify(exactly = 0) { surveyService.handle(any()) }
    }

    @Test
    fun `should process valid update and mark it completed`() {
        val update = mockUpdate(100L, 42L)
        val reply = BotReply(text = "Hello")

        every { processedUpdateStore.tryAcquire(100L) } returns true
        every { surveyService.handle(update) } returns reply
        every { processedUpdateStore.markCompleted(100L) } just Runs

        val result = useCase.execute(update)

        assertEquals(HttpStatus.OK, result.status)
        assertEquals("Hello", result.text)
        assertEquals(42L, result.chatId)
        verify { processedUpdateStore.markCompleted(100L) }
        verify(exactly = 0) { processedUpdateStore.release(any()) }
    }

    @Test
    fun `should release update when processing fails`() {
        val update = mockUpdate(100L, 42L)

        every { processedUpdateStore.tryAcquire(100L) } returns true
        every { surveyService.handle(update) } throws IllegalStateException("boom")
        every { processedUpdateStore.release(100L) } just Runs

        val result = useCase.execute(update)

        assertEquals(HttpStatus.OK, result.status)
        assertEquals(ServiceMessages.PROCESSING_FAILED_REPLY, result.text)
        verify { processedUpdateStore.release(100L) }
    }

    private fun mockUpdate(updateId: Long, chatId: Long): Update {
        val update = mockk<Update>()
        val message = mockk<Message>()

        every { update.updateId } returns updateId.toInt()
        every { update.message } returns message
        every { message.chatId } returns chatId

        return update
    }
}




