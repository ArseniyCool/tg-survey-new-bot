package telegram.commands

import org.telegram.telegrambots.meta.api.objects.Message
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.UserStates
import telegram.model.SurveyDraft
import kotlin.collections.set

fun handleGlobalCommands(
    text: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    response: Message,
): Boolean {

    when (text) {
        Commands.START.text -> {
            userStates[chatId] = UserStates.WAITING_FOR_PHONE
            drafts[chatId] = SurveyDraft()
            response.text = Answers.WELCOME.text
            return true
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            return true
        }
    }

    return false
}