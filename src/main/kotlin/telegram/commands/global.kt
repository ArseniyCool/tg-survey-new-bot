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
            // /start always restarts the flow from the beginning.
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
            // /cancel = step back (undo previous answer)
            val state = userStates[chatId]
            when (state) {
                null -> {
                    response.text = "ℹ️ Сейчас нечего отменять. Нажмите /start, чтобы начать опрос."
                    response.replyMarkup = ReplyKeyboardRemove(true)
                }

                UserStates.WAITING_FOR_PHONE -> {
                    // First step: nothing to undo, just remind.
                    response.text = "ℹ️ Вы на первом шаге. Отправьте номер телефона или нажмите \"Отправить контакт\"."
                    response.replyMarkup = phoneKeyboard()
                }

                UserStates.WAITING_FOR_PROJECT_NAME -> {
                    // Undo phone, go back to phone step.
                    val draft = drafts[chatId] ?: SurveyDraft()
                    drafts[chatId] = draft.copy(phone = null)
                    userStates[chatId] = UserStates.WAITING_FOR_PHONE

                    response.text = "⬅️ Ок, вернулись к шагу <b>телефон</b>. Отправьте номер еще раз."
                    response.replyMarkup = phoneKeyboard()
                }

                UserStates.WAITING_FOR_PURPOSE -> {
                    // Undo project name, go back to project step.
                    val draft = drafts[chatId] ?: SurveyDraft()
                    drafts[chatId] = draft.copy(projectName = null)
                    userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME

                    response.text = "⬅️ Ок, вернулись к шагу <b>название проекта</b>. Введите название проекта."
                    response.replyMarkup = ReplyKeyboardRemove(true)
                }

                UserStates.COMPLETED -> {
                    // Undo purpose, go back to purpose step.
                    val draft = drafts[chatId] ?: SurveyDraft()
                    drafts[chatId] = draft.copy(purpose = null)
                    userStates[chatId] = UserStates.WAITING_FOR_PURPOSE

                    response.text = "⬅️ Ок, вернулись к шагу <b>назначение</b>. Введите назначение проекта."
                    response.replyMarkup = ReplyKeyboardRemove(true)
                }
            }

            return true
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            return true
        }
    }

    return false
}
