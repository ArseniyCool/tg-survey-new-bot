package telegram.commands

/**
 * Обработчики глобальных команд (/start, /help, /cancel, /ping).
 * Эти команды работают независимо от текущего шага опроса.
 */

import telegram.enums.Answers
import telegram.enums.Commands
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.enums.UserStates
import java.time.Instant

fun handleGlobalCommands(
    text: String,
    session: UserSession,
    response: MutableBotReply,
): HandlingResult {

    when (text) {
        Commands.START.text -> {
            // /start всегда перезапускает опрос с самого начала.
            response.text = Answers.WELCOME.text
            response.replyMarkup = phoneKeyboard()

            return HandlingResult(
                handled = true,
                updatedSession = session.copy(
                    state = UserStates.WAITING_FOR_PHONE,
                    phone = null,
                    projectName = null,
                    purpose = null,
                    updatedAt = Instant.now(),
                )
            )
        }

        Commands.HELP.text -> {
            response.text = Answers.HELP.text
            return HandlingResult(handled = true)
        }

        Commands.CANCEL.text -> {
            // /cancel = шаг назад (отменить предыдущий ответ)
            val updated = handleCancelCommand(session, response)
            return HandlingResult(handled = true, updatedSession = updated)
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            return HandlingResult(handled = true)
        }
    }

    return HandlingResult(handled = false)
}
