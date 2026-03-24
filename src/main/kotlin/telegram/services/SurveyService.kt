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
import telegram.text.Messages

@Singleton
class SurveyService(
    private val userSessionStore: UserSessionStore,
) {
    fun handle(update: Update): BotReply {
        val incoming = parseIncomingTelegramMessage(update) ?: run {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val chatId = incoming.chatId
        val rawText = incoming.rawText
        val normalizedCommand = incoming.normalizedCommand
        val toUser = MutableBotReply()

        val loadedSession = userSessionStore.findOrCreate(chatId)
        val session = loadedSession.session

        // /forget: удалить данные пользователя и начать заново.
        if (normalizedCommand == Commands.FORGET.text) {
            userSessionStore.delete(chatId)
            toUser.text = Messages.forgetOk()
            return toUser.toImmutable()
        }

        // 1) Глобальные команды.
        val global = handleGlobalCommands(normalizedCommand ?: rawText.lowercase(), session, toUser)
        if (global.handled) {
            global.updatedSession?.let { userSessionStore.save(it, loadedSession.existed) }
            return toUser.toImmutable()
        }

        // Любое сообщение, начинающееся с '/', считаем командой. Если мы здесь — команда неизвестна.
        // Это автоматически запрещает вводить "название проекта" / "назначение", начинающиеся с '/'.
        if (normalizedCommand != null) {
            toUser.text = Messages.unknownCommand(normalizedCommand)
            return toUser.toImmutable()
        }

        // 2) Ввод по шагам (состояниям).
        val state = handleStatesCommands(rawText, session, toUser)
        if (state.handled) {
            state.updatedSession?.let { userSessionStore.save(it, loadedSession.existed) }
            return toUser.toImmutable()
        }

        // 3) Запасной вариант (если ничего не подошло).
        toUser.text = Answers.DONT_UNDERSTAND.text
        return toUser.toImmutable()
    }
}
