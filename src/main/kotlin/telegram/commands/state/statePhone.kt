package telegram.commands.state

/**
 * Обработка шага "телефон".
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.commands.HandlingResult
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.format.escapeHtml
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
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

    // Скрываем клавиатуру "отправить контакт" после того, как получили телефон.
    toUserMessage.replyMarkup = ReplyKeyboardRemove(true)

    val phoneEscaped = escapeHtml(normalizedPhone)
    toUserMessage.text =
        "✅ <b>Ваш телефон</b> <code>$phoneEscaped</code> сохранен.\n\n" +
            Answers.NUMBER_SAVED.text

    return HandlingResult(
        handled = true,
        updatedSession = session.copy(
            state = UserStates.WAITING_FOR_PROJECT_NAME,
            phone = normalizedPhone,
            updatedAt = Instant.now(),
        )
    )
}
