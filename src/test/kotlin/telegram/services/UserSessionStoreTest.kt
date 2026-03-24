package telegram.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository
import java.util.Optional

class UserSessionStoreTest {

    private val repository = mockk<UserSessionRepository>(relaxed = true)
    private val store = UserSessionStore(repository)

    @Test
    fun `find or create should return existing session`() {
        val existing = UserSession(chatId = 7L, phone = "89173967903")
        every { repository.findById(7L) } returns Optional.of(existing)

        val result = store.findOrCreate(7L)

        assertTrue(result.existed)
        assertEquals(existing, result.session)
    }

    @Test
    fun `find or create should create empty session when nothing found`() {
        every { repository.findById(8L) } returns Optional.empty()

        val result = store.findOrCreate(8L)

        assertFalse(result.existed)
        assertEquals(8L, result.session.chatId)
        assertEquals(null, result.session.state)
    }

    @Test
    fun `save should call insert for new session`() {
        val session = UserSession(chatId = 1L)
        every { repository.save(session) } returns session

        store.save(session, existed = false)

        verify(exactly = 1) { repository.save(session) }
        verify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `save should call update for existing session`() {
        val session = UserSession(chatId = 1L)
        every { repository.update(session) } returns session

        store.save(session, existed = true)

        verify(exactly = 1) { repository.update(session) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `delete should delegate to repository`() {
        store.delete(15L)

        verify(exactly = 1) { repository.deleteById(15L) }
    }
}
