package telegram.commands.state

/**
 * Обработка шага "телефон".
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.commands.phoneKeyboard
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.format.escapeHtml
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
import telegram.validation.normalizePhoneNumber
import kotlin.collections.set

internal fun handleWaitingForPhone(
    fromUserMessage: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    toUserMessage: MutableBotReply,
): Boolean {
    val normalizedPhone = normalizePhoneNumber(fromUserMessage.trim())
    if (normalizedPhone == null) {
        toUserMessage.text = Answers.INCORRECT_NUMBER.text
        return true
    }

    val draft = drafts[chatId] ?: SurveyDraft()
    drafts[chatId] = draft.copy(phone = normalizedPhone)

    // Скрываем клавиатуру "отправить контакт" после того, как получили телефон.
    toUserMessage.replyMarkup = ReplyKeyboardRemove(true)

    val phoneEscaped = escapeHtml(normalizedPhone)
    toUserMessage.text =
        "✅ <b>Ваш телефон</b> <code>$phoneEscaped</code> сохранен.\n\n" +
            Answers.NUMBER_SAVED.text

    userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME
    return true
}

