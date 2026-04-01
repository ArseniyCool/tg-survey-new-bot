package telegram.commands.state

/**
 * Обработка шага "телефон".
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.commands.HandlingResult
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.text.BotMessages
import telegram.validation.normalizePhoneNumber
import java.time.Instant

internal fun handleWaitingForPhone(
    fromUserMessage: String,
    session: UserSession,
    toUserMessage: MutableBotReply,
): HandlingResult {
    val normalizedPhone = normalizePhoneNumber(fromUserMessage.trim())
    if (normalizedPhone == null) {
        toUserMessage.text = Answers.INCORRECT_NUMBER.text
        return HandlingResult(handled = true)
    }

    toUserMessage.replyMarkup = ReplyKeyboardRemove(true)

    toUserMessage.text = BotMessages.phoneSaved(normalizedPhone) + "\n\n" + Answers.NUMBER_SAVED.text

    return HandlingResult(
        handled = true,
        updatedSession = session.copy(
            state = UserStates.WAITING_FOR_PROJECT_NAME,
            phone = normalizedPhone,
            updatedAt = Instant.now(),
        )
    )
}



