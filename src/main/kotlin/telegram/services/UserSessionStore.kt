package telegram.services

/**
 * Тонкая прослойка над репозиторием пользовательских сессий.
 *
 * Нужна, чтобы бизнес-логика опроса не зависела напрямую от деталей сохранения:
 * как получить сессию, как надежно сохранить ее в БД и как удалить данные пользователя.
 */

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository

@Singleton
class UserSessionStore(
    private val sessions: UserSessionRepository,
) {
    private val log = LoggerFactory.getLogger(UserSessionStore::class.java)

    fun findOrCreate(chatId: Long): UserSession {
        val session = sessions.findById(chatId).orElse(UserSession(chatId = chatId))
        log.debug(
            "event=session_loaded chatId={} state={} hasPhone={} hasProjectName={} hasPurpose={}",
            chatId,
            session.state,
            session.phone != null,
            session.projectName != null,
            session.purpose != null,
        )
        return session
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

        log.info(
            "event=session_saved chatId={} state={} hasPhone={} hasProjectName={} hasPurpose={}",
            session.chatId,
            session.state,
            session.phone != null,
            session.projectName != null,
            session.purpose != null,
        )
    }

    fun delete(chatId: Long) {
        sessions.deleteById(chatId)
        log.info("event=session_deleted chatId={}", chatId)
    }
}
