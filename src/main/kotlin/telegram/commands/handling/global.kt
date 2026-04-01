package telegram.commands.handling

/**
 * Обработка глобальных команд.
 */

import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.text.BotMessages
import telegram.commands.HandlingResult
import telegram.commands.phoneKeyboard
import java.time.Instant

fun handleGlobalCommands(
    text: String,
    session: UserSession,
    response: MutableBotReply,
): HandlingResult {

    return when (text) {
        Commands.START.text -> {
            response.text = Answers.WELCOME.text
            response.replyMarkup = phoneKeyboard()

            HandlingResult(
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
            HandlingResult(handled = true)
        }

        Commands.PRIVACY.text -> {
            response.text = Answers.PRIVACY.text
            HandlingResult(handled = true)
        }

        Commands.CANCEL.text -> {
            val updated = handleCancelCommand(session, response)
            HandlingResult(handled = true, updatedSession = updated)
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            HandlingResult(handled = true)
        }

        Commands.CHECK.text, Commands.STATUS.text -> {
            response.text = BotMessages.checkStatus(session)
            HandlingResult(handled = true)
        }

        else -> HandlingResult(handled = false)
    }
}




