package telegram.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ServiceMessagesTest {

    @Test
    fun `should keep command registration logs in one place`() {
        assertEquals(
            "telegram.token пустой; регистрацию меню команд пропускаем",
            ServiceMessages.EMPTY_TOKEN_SKIP_COMMANDS_REGISTRATION_LOG
        )
        assertEquals(
            "Меню команд Telegram успешно зарегистрировано",
            ServiceMessages.COMMANDS_REGISTRATION_SUCCESS_LOG
        )
        assertEquals(
            "Не удалось зарегистрировать меню команд Telegram (игнорируем)",
            ServiceMessages.COMMANDS_REGISTRATION_EXCEPTION_LOG
        )
    }

    @Test
    fun `should keep configured processing failed reply in one place`() {
        assertEquals("⚠️ Произошла ошибка. Попробуйте позже.", ServiceMessages.PROCESSING_FAILED_REPLY)
    }

    @Test
    fun `should keep invalid secret log template with header placeholder`() {
        assertTrue(ServiceMessages.INVALID_SECRET_LOG.contains("remote-header-present={}"))
    }
}
