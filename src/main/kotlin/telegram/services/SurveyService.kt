package telegram.services

/**
 * Основная логика опроса: принимает Update, управляет состояниями и черновиком.
 */

import jakarta.inject.Singleton
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.commands.handleGlobalCommands
import telegram.commands.handleStatesCommands
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.model.BotReply
import telegram.model.MutableBotReply
import telegram.persistence.SurveySubmission
import telegram.persistence.SurveySubmissionRepository
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository
import java.time.Instant

@Singleton
class SurveyService(
    private val repository: SurveySubmissionRepository,
    private val sessions: UserSessionRepository,
) {
    fun handle(update: Update): BotReply {
        val incoming = update.message
        if (incoming == null) {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val chatId = incoming.chatId
        val contactPhone = incoming.contact?.phoneNumber
        val incomingText = incoming.text

        // Поддерживаем либо обычный текст, либо отправку контакта (телефон).
        if (incomingText == null && contactPhone == null) {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val rawText = (incomingText ?: contactPhone ?: return BotReply(text = Answers.DONT_UNDERSTAND.text)).trim()
        val normalizedText = rawText.lowercase()
        val toUser = MutableBotReply()

        val session = sessions.findById(chatId).orElse(UserSession(chatId = chatId))

        // /forget: удалить данные пользователя (сессия + все анкеты) и начать заново.
        if (normalizedText == Commands.FORGET.text) {
            sessions.deleteById(chatId)
            repository.deleteByChatId(chatId)

            toUser.text =
                "🗑️ <b>Ваши данные удалены.</b>\n\n" +
                    "Можете начать опрос заново: /start"
            return toUser.toImmutable()
        }

        // 1) Глобальные команды: сравниваем в нормализованном виде, чтобы работало /StArT и т.п.
        val global = handleGlobalCommands(normalizedText, session, toUser)
        if (global.handled) {
            global.updatedSession?.let { sessions.save(it) }
            return toUser.toImmutable()
        }

        // 2) Ввод по шагам (состояниям): для названия/назначения сохраняем исходный регистр.
        val state = handleStatesCommands(rawText, session, toUser) { id, completed ->
            repository.save(
                SurveySubmission(
                    id = null,
                    chatId = id,
                    phone = completed.phone ?: "",
                    projectName = completed.projectName ?: "",
                    purpose = completed.purpose ?: "",
                    createdAt = Instant.now(),
                )
            )
        }
        if (state.handled) {
            state.updatedSession?.let { sessions.save(it) }
            return toUser.toImmutable()
        }

        // 3) Запасной вариант (если ничего не подошло)
        toUser.text = Answers.DONT_UNDERSTAND.text
        return toUser.toImmutable()
    }
}

