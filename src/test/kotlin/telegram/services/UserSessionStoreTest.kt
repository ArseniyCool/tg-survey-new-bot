package telegram.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import telegram.enums.UserStates
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository
import java.time.Instant
import java.util.Optional

class UserSessionStoreTest {

    private val repository = mockk<UserSessionRepository>(relaxed = true)
    private val store = UserSessionStore(repository)

    @Test
    fun `find or create should return existing session`() {
        val existing = UserSession(chatId = 7L, phone = "89173967903")
        every { repository.findById(7L) } returns Optional.of(existing)

        val result = store.findOrCreate(7L)

        assertEquals(existing, result)
    }

    @Test
    fun `find or create should create empty session when nothing found`() {
        every { repository.findById(8L) } returns Optional.empty()

        val result = store.findOrCreate(8L)

        assertEquals(8L, result.chatId)
        assertEquals(null, result.state)
    }

    @Test
    fun `save should call repository upsert`() {
        val updatedAt = Instant.now()
        val session = UserSession(
            chatId = 1L,
            state = UserStates.WAITING_FOR_PROJECT_NAME,
            phone = "89173967903",
            projectName = "Project",
            purpose = "Purpose",
            updatedAt = updatedAt,
        )

        store.save(session)

        verify(exactly = 1) {
            repository.upsert(
                1L,
                UserStates.WAITING_FOR_PROJECT_NAME.name,
                "89173967903",
                "Project",
                "Purpose",
                updatedAt,
            )
        }
    }

    @Test
    fun `delete should delegate to repository`() {
        store.delete(15L)

        verify(exactly = 1) { repository.deleteById(15L) }
    }
}
