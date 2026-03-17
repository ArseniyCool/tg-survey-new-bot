package telegram.commands

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
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

            toUserMessage.text = Answers.NUMBER_SAVED.text
            userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME
            return true
        }

        UserStates.WAITING_FOR_PROJECT_NAME -> {
            val projectName = fromUserMessage.trim()
            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(projectName = projectName)

            toUserMessage.text = Answers.PROJECT_SAVED.text
            userStates[chatId] = UserStates.WAITING_FOR_PURPOSE
            return true
        }

        UserStates.WAITING_FOR_PURPOSE -> {
            val purpose = fromUserMessage.trim()
            val draft = drafts[chatId] ?: SurveyDraft()
            val completed = draft.copy(purpose = purpose)

            // Persist before clearing in-memory state.
            onCompleted(chatId, completed)

            toUserMessage.text = Answers.PURPOSE_SAVED.text
            userStates.remove(chatId)
            drafts.remove(chatId)
            return true
        }
    }
}
