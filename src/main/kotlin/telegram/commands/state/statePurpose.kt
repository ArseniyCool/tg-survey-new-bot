package telegram.commands.state

/**
 * Обработка шага "назначение" и формирование итоговой квитанции.
 */

import telegram.commands.HandlingResult
import telegram.enums.Answers
import telegram.enums.InputLimit
import telegram.enums.UserStates
import telegram.format.escapeHtml
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
import telegram.persistence.UserSession
import telegram.validation.containsEmoji
import telegram.validation.isLengthInRange
import java.time.Instant

internal fun handleWaitingForPurpose(
    fromUserMessage: String,
    session: UserSession,
    toUserMessage: MutableBotReply,
    onCompleted: (Long, SurveyDraft) -> Unit,
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

    val completedDraft = SurveyDraft(
        phone = completedSession.phone,
        projectName = completedSession.projectName,
        purpose = completedSession.purpose,
    )

    // Сохраняем в базу до перехода в COMPLETED.
    onCompleted(completedSession.chatId, completedDraft)

    val phone = escapeHtml(completedSession.phone ?: "")
    val project = escapeHtml(completedSession.projectName ?: "")
    val purposeEscaped = escapeHtml(completedSession.purpose ?: "")

    toUserMessage.text =
        "🧾 <b>Спасибо за заполнение анкеты!</b>\n" +
            "✅ Все сохранено.\n\n" +
            "📱 <b>Телефон:</b> <code>$phone</code>\n" +
            "📦 <b>Проект:</b> <code>$project</code>\n" +
            "🎯 <b>Назначение:</b> <code>$purposeEscaped</code>\n\n" +
            "🔁 Заполнить заново? /start\n" +
            "⬅️ Шаг назад: /cancel"

    return HandlingResult(handled = true, updatedSession = completedSession)
}
