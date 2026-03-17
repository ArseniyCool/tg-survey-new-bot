package telegram.commands

/**
 * Логика команды /cancel.
 *
 * У нас /cancel означает "шаг назад": отменить последний введенный ответ и вернуться на предыдущий шаг опроса.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft

internal fun handleCancelCommand(
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    response: MutableBotReply,
) {
    val state = userStates[chatId]
    when (state) {
        null -> {
            response.text = "ℹ️ Сейчас нечего отменять. Нажмите /start, чтобы начать опрос."
            response.replyMarkup = ReplyKeyboardRemove(true)
        }

        UserStates.WAITING_FOR_PHONE -> {
            // Первый шаг: отменять нечего, просто подсказываем.
            response.text = "ℹ️ Вы на первом шаге. Отправьте номер телефона или нажмите \"Отправить контакт\"."
            response.replyMarkup = phoneKeyboard()
        }

        UserStates.WAITING_FOR_PROJECT_NAME -> {
            // Отменяем телефон, возвращаемся на шаг телефона.
            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(phone = null)
            userStates[chatId] = UserStates.WAITING_FOR_PHONE

            response.text = "⬅️ Ок, вернулись к шагу <b>телефон</b>. Отправьте номер еще раз."
            response.replyMarkup = phoneKeyboard()
        }

        UserStates.WAITING_FOR_PURPOSE -> {
            // Отменяем название проекта, возвращаемся на шаг названия.
            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(projectName = null)
            userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME

            response.text = "⬅️ Ок, вернулись к шагу <b>название проекта</b>. Введите название проекта."
            response.replyMarkup = ReplyKeyboardRemove(true)
        }

        UserStates.COMPLETED -> {
            // Отменяем назначение, возвращаемся на шаг назначения.
            val draft = drafts[chatId] ?: SurveyDraft()
            drafts[chatId] = draft.copy(purpose = null)
            userStates[chatId] = UserStates.WAITING_FOR_PURPOSE

            response.text = "⬅️ Ок, вернулись к шагу <b>назначение</b>. Введите назначение проекта."
            response.replyMarkup = ReplyKeyboardRemove(true)
        }
    }
}

