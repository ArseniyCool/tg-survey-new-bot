package telegram.commands.state

/**
 * Обработка шага "назначение" и формирование итоговой квитанции.
 */

import telegram.commands.HandlingResult
import telegram.enums.Answers
import telegram.enums.InputLimit
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.text.BotMessages
import telegram.validation.containsEmoji
import telegram.validation.isLengthInRange
import java.time.Instant

internal fun handleWaitingForPurpose(
    fromUserMessage: String,
    session: UserSession,
    toUserMessage: MutableBotReply,
): HandlingResult {
    val purpose = fromUserMessage.trim()

    if (!isLengthInRange(purpose, InputLimit.PURPOSE.min, InputLimit.PURPOSE.max)) {
        toUserMessage.text = Answers.PURPOSE_LENGTH_INVALID.text
        return HandlingResult(handled = true)
    }

    if (containsEmoji(purpose)) {
        toUserMessage.text = Answers.EMOJI_NOT_ALLOWED.text
        return HandlingResult(handled = true)
    }

    val completedSession = session.copy(
        state = UserStates.COMPLETED,
        purpose = purpose,
        updatedAt = Instant.now(),
    )

    toUserMessage.text = BotMessages.receipt(
        phone = completedSession.phone.orEmpty(),
        projectName = completedSession.projectName.orEmpty(),
        purpose = completedSession.purpose.orEmpty(),
    )

    return HandlingResult(handled = true, updatedSession = completedSession)
}



