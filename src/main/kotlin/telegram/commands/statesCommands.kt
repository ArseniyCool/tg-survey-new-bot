package telegram.commands

/**
 * Обработка пользовательского ввода по шагам опроса (состояния).
 * Здесь выполняются проверки и формируются ответы/квитанция.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.enums.Answers
import telegram.enums.InputLimit
import telegram.enums.UserStates
import telegram.format.escapeHtml
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
import telegram.validation.containsEmoji
import telegram.validation.isLengthInRange
import telegram.validation.normalizePhoneNumber
import kotlin.collections.set

fun handleStatesCommands(
    fromUserMessage: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    toUserMessage: MutableBotReply,
    onCompleted: (Long, SurveyDraft) -> Unit,
): Boolean {

    val state = userStates[chatId] ?: return false

    when (state) {
        UserStates.WAITING_FOR_PHONE -> {
            val normalizedPhone = normalizePhoneNumber(fromUserMessage.trim())
            if (normalizedPhone == null) {
                toUserMessage.text = Answers.INCORRECT_NUMBER.text
                return true
            }

            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(phone = normalizedPhone)

            // Hide the "share contact" keyboard after we got the phone.
            toUserMessage.replyMarkup = ReplyKeyboardRemove(true)

            val phoneEscaped = escapeHtml(normalizedPhone)
            toUserMessage.text =
                "✅ <b>Ваш телефон</b> <code>$phoneEscaped</code> сохранен.\n\n" +
                    Answers.NUMBER_SAVED.text

            userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME
            return true
        }

        UserStates.WAITING_FOR_PROJECT_NAME -> {
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

        UserStates.WAITING_FOR_PURPOSE -> {
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

            // Persist before switching to COMPLETED.
            onCompleted(chatId, completed)

            // Keep draft/state so user can go back and adjust the last answer via /cancel.
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

        UserStates.COMPLETED -> return false
    }
}

