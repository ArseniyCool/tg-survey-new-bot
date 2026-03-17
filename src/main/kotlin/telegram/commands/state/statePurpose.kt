package telegram.commands.state

/**
 * Обработка шага "назначение" и формирование итоговой квитанции.
 */

import telegram.enums.Answers
import telegram.enums.InputLimit
import telegram.enums.UserStates
import telegram.format.escapeHtml
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
import telegram.validation.containsEmoji
import telegram.validation.isLengthInRange
import kotlin.collections.set

internal fun handleWaitingForPurpose(
    fromUserMessage: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    toUserMessage: MutableBotReply,
    onCompleted: (Long, SurveyDraft) -> Unit,
): Boolean {
    val purpose = fromUserMessage.trim()

    if (!isLengthInRange(purpose, InputLimit.PURPOSE.min, InputLimit.PURPOSE.max)) {
        toUserMessage.text = Answers.PURPOSE_LENGTH_INVALID.text
        return true
    }

    if (containsEmoji(purpose)) {
        toUserMessage.text = Answers.EMOJI_NOT_ALLOWED.text
        return true
    }

    val draft = drafts[chatId] ?: SurveyDraft()
    val completed = draft.copy(purpose = purpose)

    // Сохраняем в базу до перехода в COMPLETED.
    onCompleted(chatId, completed)

    // Оставляем черновик/состояние, чтобы пользователь мог вернуться назад и исправить ответ через /cancel.
    drafts[chatId] = completed
    userStates[chatId] = UserStates.COMPLETED

    val phone = escapeHtml(completed.phone ?: "")
    val project = escapeHtml(completed.projectName ?: "")
    val purposeEscaped = escapeHtml(completed.purpose ?: "")

    toUserMessage.text =
        "🧾 <b>Спасибо за заполнение анкеты!</b>\n" +
            "✅ Все сохранено.\n\n" +
            "📱 <b>Телефон:</b> <code>$phone</code>\n" +
            "📦 <b>Проект:</b> <code>$project</code>\n" +
            "🎯 <b>Назначение:</b> <code>$purposeEscaped</code>\n\n" +
            "🔁 Заполнить заново? /start\n" +
            "⬅️ Шаг назад: /cancel"
    return true
}

