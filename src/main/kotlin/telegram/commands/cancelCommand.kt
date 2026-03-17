package telegram.commands

/**
 * Логика команды /cancel.
 *
 * У нас /cancel означает "шаг назад": отменить последний введенный ответ и вернуться на предыдущий шаг опроса.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft

private data class CancelBackStep(
    val stateToReturn: UserStates,
    val clearDraft: (SurveyDraft) -> SurveyDraft,
    val message: String,
    val replyMarkup: () -> ReplyKeyboard?,
)

/**
 * Шаги для команды /cancel по индексу шага: state.stepIndex - 1.
 *
 * Индекс 0 = шаг "телефон", индекс 1 = "название проекта", индекс 2 = "назначение".
 * COMPLETED имеет stepIndex=3 и возвращает на индекс 2 (назначение).
 */
private val cancelBackSteps: List<CancelBackStep> = listOf(
    CancelBackStep(
        stateToReturn = UserStates.WAITING_FOR_PHONE,
        clearDraft = { draft -> draft.copy(phone = null) },
        message = "⬅️ Ок, вернулись к шагу <b>телефон</b>. Отправьте номер еще раз.",
        replyMarkup = { phoneKeyboard() },
    ),
    CancelBackStep(
        stateToReturn = UserStates.WAITING_FOR_PROJECT_NAME,
        clearDraft = { draft -> draft.copy(projectName = null) },
        message = "⬅️ Ок, вернулись к шагу <b>название проекта</b>. Введите название проекта.",
        replyMarkup = { ReplyKeyboardRemove(true) },
    ),
    CancelBackStep(
        stateToReturn = UserStates.WAITING_FOR_PURPOSE,
        clearDraft = { draft -> draft.copy(purpose = null) },
        message = "⬅️ Ок, вернулись к шагу <b>назначение</b>. Введите назначение проекта.",
        replyMarkup = { ReplyKeyboardRemove(true) },
    ),
)

internal fun handleCancelCommand(
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    response: MutableBotReply,
) {
    val state = userStates[chatId]

    if (state == null) {
        response.text = "ℹ️ Сейчас нечего отменять. Нажмите /start, чтобы начать опрос."
        response.replyMarkup = ReplyKeyboardRemove(true)
        return
    }

    // Если мы на самом первом шаге, то "шаг назад" делать некуда.
    if (state.stepIndex == UserStates.WAITING_FOR_PHONE.stepIndex) {
        response.text = "ℹ️ Вы на первом шаге. Отправьте номер телефона или нажмите \"Отправить контакт\"."
        response.replyMarkup = phoneKeyboard()
        return
    }

    val currentIndex = state.stepIndex
    val returnIndex = currentIndex - 1

    // Шаг, на который возвращаемся, определяется автоматически: "на 1 меньше".
    // Важно: при откате мы очищаем поле черновика, которое относится к этому шагу.
    val back = cancelBackSteps.getOrNull(returnIndex)
    if (back == null) {
        // На всякий случай: если конфигурация шагов изменилась, не падаем.
        response.text = "ℹ️ Сейчас нечего отменять. Нажмите /start, чтобы начать опрос."
        response.replyMarkup = ReplyKeyboardRemove(true)
        return
    }

    val draft = drafts[chatId] ?: SurveyDraft()
    drafts[chatId] = back.clearDraft(draft)
    userStates[chatId] = back.stateToReturn
    response.text = back.message
    response.replyMarkup = back.replyMarkup()
}
