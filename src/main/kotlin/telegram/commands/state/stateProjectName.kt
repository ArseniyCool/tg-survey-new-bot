package telegram.commands.state

/**
 * Обработка шага "название проекта".
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

internal fun handleWaitingForProjectName(
    fromUserMessage: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    toUserMessage: MutableBotReply,
): Boolean {
    val projectName = fromUserMessage.trim()

    if (!isLengthInRange(projectName, InputLimit.PROJECT_NAME.min, InputLimit.PROJECT_NAME.max)) {
        toUserMessage.text = Answers.PROJECT_NAME_LENGTH_INVALID.text
        return true
    }

    if (containsEmoji(projectName)) {
        toUserMessage.text = Answers.EMOJI_NOT_ALLOWED.text
        return true
    }

    val draft = drafts[chatId] ?: SurveyDraft()
    drafts[chatId] = draft.copy(projectName = projectName)

    val projectEscaped = escapeHtml(projectName)
    toUserMessage.text =
        "✅ <b>Ваш проект</b> <code>$projectEscaped</code> сохранен.\n\n" +
            Answers.PROJECT_SAVED.text

    userStates[chatId] = UserStates.WAITING_FOR_PURPOSE
    return true
}

