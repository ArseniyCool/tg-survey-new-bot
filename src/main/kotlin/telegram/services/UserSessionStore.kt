package telegram.services

/**
 * Тонкая прослойка над репозиторием пользовательских сессий.
 *
 * Нужна, чтобы бизнес-логика опроса не зависела напрямую от деталей сохранения:
 * как получить сессию, когда делать save/update и как удалить данные пользователя.
 */

import jakarta.inject.Singleton
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository

@Singleton
class UserSessionStore(
    private val sessions: UserSessionRepository,
) {
    fun findOrCreate(chatId: Long): UserSessionLoadResult {
        val sessionOpt = sessions.findById(chatId)
        val session = sessionOpt.orElse(UserSession(chatId = chatId))

        return UserSessionLoadResult(
            session = session,
            existed = sessionOpt.isPresent,
        )
    }

    fun save(session: UserSession, existed: Boolean) {
        if (existed) {
            sessions.update(session)
        } else {
            sessions.save(session)
        }
    }

    fun delete(chatId: Long) {
        sessions.deleteById(chatId)
    }
}

data class UserSessionLoadResult(
    val session: UserSession,
    val existed: Boolean,
)
