package telegram.services

/**
 * Основная логика опроса: принимает Telegram Update, обрабатывает команды и шаги опроса.
 *
 * Состояние и введенные пользователем данные хранятся в БД в одной таблице `user_sessions`.
 */

import jakarta.inject.Singleton
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.commands.handleGlobalCommands
import telegram.commands.handleStatesCommands
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.model.BotReply
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository
import telegram.text.Messages

@Singleton
class SurveyService(
    private val sessions: UserSessionRepository,
) {
    fun handle(update: Update): BotReply {
        val incoming = update.message ?: return BotReply(text = Answers.DONT_UNDERSTAND.text)

        val chatId = incoming.chatId
        val contactPhone = incoming.contact?.phoneNumber
        val incomingText = incoming.text

        // Поддерживаем либо обычный текст, либо отправку контакта (телефон).
        if (incomingText == null && contactPhone == null) {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val rawText = (incomingText ?: contactPhone ?: return BotReply(text = Answers.DONT_UNDERSTAND.text)).trim()
        val normalizedText = rawText.lowercase()

        // Telegram (особенно в группах) может присылать команды вида "/start@MyBot",
        // а также команды с параметрами: "/start foo". Для сравнения команд выделяем "чистую" команду.
        val normalizedCommand = if (normalizedText.startsWith("/")) {
            normalizedText
                .split(Regex("\\s+"), limit = 2)[0]
                .substringBefore("@")
        } else {
            normalizedText
        }

        val toUser = MutableBotReply()

        val sessionOpt = sessions.findById(chatId)
        val sessionExists = sessionOpt.isPresent
        val session = sessionOpt.orElse(UserSession(chatId = chatId))

        fun persistSession(updated: UserSession) {
            // Для сущностей с "назначаемым" PK (chat_id) save() может попытаться INSERT,
            // поэтому при наличии записи делаем UPDATE.
            if (sessionExists) {
                sessions.update(updated)
            } else {
                sessions.save(updated)
            }
        }

        // /forget: удалить данные пользователя (сессия) и начать заново.
        if (normalizedCommand == Commands.FORGET.text) {
            sessions.deleteById(chatId)
            toUser.text = Messages.forgetOk()
            return toUser.toImmutable()
        }

        // 1) Глобальные команды.
        val global = handleGlobalCommands(normalizedCommand, session, toUser)
        if (global.handled) {
            global.updatedSession?.let { persistSession(it) }
            return toUser.toImmutable()
        }

        // Любое сообщение, начинающееся с '/', считаем командой. Если мы здесь — команда неизвестна.
        // Это автоматически запрещает вводить "название проекта" / "назначение", начинающиеся с '/'.
        if (normalizedCommand.startsWith("/")) {
            toUser.text = Messages.unknownCommand(normalizedCommand)
            return toUser.toImmutable()
        }

        // 2) Ввод по шагам (состояниям).
        val state = handleStatesCommands(rawText, session, toUser)
        if (state.handled) {
            state.updatedSession?.let { persistSession(it) }
            return toUser.toImmutable()
        }

        // 3) Запасной вариант (если ничего не подошло).
        toUser.text = Answers.DONT_UNDERSTAND.text
        return toUser.toImmutable()
    }
}

