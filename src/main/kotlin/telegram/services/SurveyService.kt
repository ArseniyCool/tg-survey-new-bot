package telegram.services

/**
 * Основная логика опроса: принимает Telegram Update, обрабатывает команды и шаги опроса.
 *
 * Состояние и введенные пользователем данные хранятся в БД в таблице `user_sessions`.
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
        val session = userSessionStore.findOrCreate(chatId)

        if (normalizedCommand == Commands.FORGET.text) {
            userSessionStore.delete(chatId)
            toUser.text = Messages.forgetOk()
            return toUser.toImmutable()
        }

        val global = handleGlobalCommands(normalizedCommand ?: rawText.lowercase(), session, toUser)
        if (global.handled) {
            global.updatedSession?.let { userSessionStore.save(it) }
            return toUser.toImmutable()
        }

        if (normalizedCommand != null) {
            toUser.text = Messages.unknownCommand(normalizedCommand)
            return toUser.toImmutable()
        }

        val state = handleStatesCommands(rawText, session, toUser)
        if (state.handled) {
            state.updatedSession?.let { userSessionStore.save(it) }
            return toUser.toImmutable()
        }

        toUser.text = Answers.DONT_UNDERSTAND.text
        return toUser.toImmutable()
    }
}
