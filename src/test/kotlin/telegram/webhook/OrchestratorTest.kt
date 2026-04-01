package telegram.webhook

import io.micronaut.http.HttpStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class OrchestratorTest {

    private val accessPolicy = mockk<AccessPolicy>()
    private val processTelegramUpdateUseCase = mockk<ProcessTelegramUpdateUseCase>()
    private val orchestrator = Orchestrator(
        accessPolicy,
        processTelegramUpdateUseCase,
    )

    @Test
    fun `should return forbidden result when webhook security is misconfigured`() {
        val update = mockUpdate(100L, 42L)
        every { accessPolicy.authorize(null, null) } returns AccessDecision.MISCONFIGURED

        val result = orchestrator.process(update, null, null)

        assertEquals(HttpStatus.FORBIDDEN, result.status)
        verify(exactly = 0) { processTelegramUpdateUseCase.execute(any()) }
    }

    @Test
    fun `should return forbidden result when webhook token is invalid`() {
        val update = mockUpdate(100L, 42L)
        every { accessPolicy.authorize("bad", null) } returns AccessDecision.DENIED

        val result = orchestrator.process(update, "bad", null)

        assertEquals(HttpStatus.FORBIDDEN, result.status)
        verify(exactly = 0) { processTelegramUpdateUseCase.execute(any()) }
    }

    @Test
    fun `should delegate allowed update to use case`() {
        val update = mockUpdate(100L, 42L)
        val expectedResult = ProcessingResult.reply(42L, "Hello")

        every { accessPolicy.authorize(null, null) } returns AccessDecision.ALLOWED
        every { processTelegramUpdateUseCase.execute(update) } returns expectedResult

        val result = orchestrator.process(update, null, null)

        assertEquals(expectedResult, result)
        verify { processTelegramUpdateUseCase.execute(update) }
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


