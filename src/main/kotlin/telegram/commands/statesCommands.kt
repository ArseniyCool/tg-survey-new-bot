package telegram.commands

import org.telegram.telegrambots.meta.api.objects.Message
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.model.SurveyDraft
import telegram.validation.isValidPhoneNumber
import kotlin.collections.set

fun handleStatesCommands(
    fromUserMessage: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    toUserMessage: Message,
): Boolean {

    val state = userStates[chatId] ?: return false

    when (state) {
        UserStates.WAITING_FOR_PHONE -> {
            if (!isValidPhoneNumber(fromUserMessage)) {
                toUserMessage.text = Answers.INCORRECT_NUMBER.text
                return true
            }

            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(phone = fromUserMessage)

            toUserMessage.text = Answers.NUMBER_SAVED.text
            userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME
            return true
        }

        UserStates.WAITING_FOR_PROJECT_NAME -> {
            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(projectName = fromUserMessage)

            toUserMessage.text = Answers.PROJECT_SAVED.text
            userStates[chatId] = UserStates.WAITING_FOR_PURPOSE
            return true
        }

        UserStates.WAITING_FOR_PURPOSE -> {
            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(purpose = fromUserMessage)

            toUserMessage.text = Answers.PURPOSE_SAVED.text
            userStates.remove(chatId)
            drafts.remove(chatId)
            return true
        }
    }
}