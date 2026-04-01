package telegram.commands.handling

/**
 * Обработка пользовательского ввода по шагам опроса.
 */

import telegram.commands.state.handleWaitingForPhone
import telegram.commands.state.handleWaitingForProjectName
import telegram.commands.state.handleWaitingForPurpose
import telegram.commands.HandlingResult
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession

fun handleStatesCommands(
    fromUserMessage: String,
    session: UserSession,
    toUserMessage: MutableBotReply,
): HandlingResult {
    val state = session.state ?: return HandlingResult(handled = false)

    return when (state) {
        UserStates.WAITING_FOR_PHONE -> handleWaitingForPhone(fromUserMessage, session, toUserMessage)
        UserStates.WAITING_FOR_PROJECT_NAME -> handleWaitingForProjectName(fromUserMessage, session, toUserMessage)
        UserStates.WAITING_FOR_PURPOSE -> handleWaitingForPurpose(fromUserMessage, session, toUserMessage)
        UserStates.COMPLETED -> HandlingResult(handled = false)
    }
}




