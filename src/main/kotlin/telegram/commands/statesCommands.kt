package telegram.commands

/**
 * Обработка пользовательского ввода по шагам опроса (состояния).
 * Здесь выполняются проверки и формируются ответы/квитанция.
 */

import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.commands.state.handleWaitingForPhone
import telegram.commands.state.handleWaitingForProjectName
import telegram.commands.state.handleWaitingForPurpose
import telegram.persistence.UserSession

fun handleStatesCommands(
    fromUserMessage: String,
    session: UserSession,
    toUserMessage: MutableBotReply,
): HandlingResult {

    val state = session.state ?: return HandlingResult(handled = false)

    when (state) {
        UserStates.WAITING_FOR_PHONE -> {
            return handleWaitingForPhone(fromUserMessage, session, toUserMessage)
        }

        UserStates.WAITING_FOR_PROJECT_NAME -> {
            return handleWaitingForProjectName(fromUserMessage, session, toUserMessage)
        }

        UserStates.WAITING_FOR_PURPOSE -> {
            return handleWaitingForPurpose(fromUserMessage, session, toUserMessage)
        }

        UserStates.COMPLETED -> return HandlingResult(handled = false)
    }
}

