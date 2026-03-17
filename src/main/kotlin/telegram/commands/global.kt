package telegram.commands

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
import kotlin.collections.set

private fun phoneKeyboard(): ReplyKeyboardMarkup {
    val button = KeyboardButton("Отправить контакт")
    button.requestContact = true

    val row = KeyboardRow()
    row.add(button)

    return ReplyKeyboardMarkup(listOf(row)).apply {
        resizeKeyboard = true
        oneTimeKeyboard = true
        selective = true
    }
}

fun handleGlobalCommands(
    text: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    response: MutableBotReply,
): Boolean {

    when (text) {
        Commands.START.text -> {
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
            userStates.remove(chatId)
            drafts.remove(chatId)
            response.text = Answers.CANCELLED.text
            response.replyMarkup = ReplyKeyboardRemove(true)
            return true
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            return true
        }
    }

    return false
}
