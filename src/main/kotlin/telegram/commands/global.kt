package telegram.commands

/**
 * Обработчики глобальных команд (/start, /help, /cancel, /ping).
 * Эти команды работают независимо от текущего шага опроса.
 */

import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft

fun handleGlobalCommands(
    text: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    response: MutableBotReply,
): Boolean {

    when (text) {
        Commands.START.text -> {
            // /start всегда перезапускает опрос с самого начала.
            userStates[chatId] = UserStates.WAITING_FOR_PHONE
            drafts[chatId] = SurveyDraft()
            response.text = Answers.WELCOME.text
            response.replyMarkup = phoneKeyboard()
            return true
        }

        Commands.HELP.text -> {
            response.text = Answers.HELP.text
            return true
        }

        Commands.CANCEL.text -> {
            // /cancel = шаг назад (отменить предыдущий ответ)
            handleCancelCommand(chatId, userStates, drafts, response)
            return true
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            return true
        }
    }

    return false
}
