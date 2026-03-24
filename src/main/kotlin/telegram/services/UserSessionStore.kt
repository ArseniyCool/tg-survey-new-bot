package telegram.services

/**
 * Тонкая прослойка над репозиторием пользовательских сессий.
 *
 * Нужна, чтобы бизнес-логика опроса не зависела напрямую от деталей сохранения:
 * как получить сессию, как надежно сохранить ее в БД и как удалить данные пользователя.
 */

import jakarta.inject.Singleton
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository

@Singleton
class UserSessionStore(
    private val sessions: UserSessionRepository,
) {
    fun findOrCreate(chatId: Long): UserSession {
        return sessions.findById(chatId).orElse(UserSession(chatId = chatId))
    }

    fun save(session: UserSession) {
        sessions.upsert(
            chatId = session.chatId,
            state = session.state?.name,
            phone = session.phone,
            projectName = session.projectName,
            purpose = session.purpose,
            updatedAt = session.updatedAt,
        )
    }

    fun delete(chatId: Long) {
        sessions.deleteById(chatId)
    }
}
