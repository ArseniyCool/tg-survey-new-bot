package telegram.commands.state

/**
 * Обработка шага "название проекта".
 */

import telegram.commands.HandlingResult
import telegram.enums.Answers
import telegram.enums.InputLimit
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.text.Messages
import telegram.validation.containsEmoji
import telegram.validation.isLengthInRange
import java.time.Instant

internal fun handleWaitingForProjectName(
    fromUserMessage: String,
    session: UserSession,
    toUserMessage: MutableBotReply,
): HandlingResult {
    val projectName = fromUserMessage.trim()

    if (!isLengthInRange(projectName, InputLimit.PROJECT_NAME.min, InputLimit.PROJECT_NAME.max)) {
        toUserMessage.text = Answers.PROJECT_NAME_LENGTH_INVALID.text
        return HandlingResult(handled = true)
    }

    if (containsEmoji(projectName)) {
        toUserMessage.text = Answers.EMOJI_NOT_ALLOWED.text
        return HandlingResult(handled = true)
    }

    toUserMessage.text = Messages.projectSaved(projectName) + "\n\n" + Answers.PROJECT_SAVED.text

    return HandlingResult(
        handled = true,
        updatedSession = session.copy(
            state = UserStates.WAITING_FOR_PURPOSE,
            projectName = projectName,
            updatedAt = Instant.now(),
        )
    )
}

