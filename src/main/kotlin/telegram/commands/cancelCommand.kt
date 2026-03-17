package telegram.commands

/**
 * Логика команды /cancel.
 *
 * У нас /cancel означает "шаг назад": отменить последний введенный ответ и вернуться на предыдущий шаг опроса.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft

private const val NOTHING_TO_CANCEL_TEXT = "ℹ️ Сейчас нечего отменять. Нажмите /start, чтобы начать опрос."

private data class CancelStep(
    val stateToReturn: UserStates,
    val clearDraft: (SurveyDraft) -> SurveyDraft,
    val stepName: String,
    val prompt: String,
)

private fun replyMarkupForState(stateToReturn: UserStates): ReplyKeyboard? {
    return if (stateToReturn == UserStates.WAITING_FOR_PHONE) {
        phoneKeyboard()
    } else {
        ReplyKeyboardRemove(true)
    }
}

/**
 * Шаги для команды /cancel по индексу шага: state.stepIndex - 1.
 *
 * Индекс 0 = шаг "телефон", индекс 1 = "название проекта", индекс 2 = "назначение".
 * COMPLETED имеет stepIndex=3 и возвращает на индекс 2 (назначение).
 */
private val cancelSteps: List<CancelStep> = listOf(
    CancelStep(
        stateToReturn = UserStates.WAITING_FOR_PHONE,
        clearDraft = { draft -> draft.copy(phone = null) },
        stepName = "телефон",
        prompt = "Отправьте номер еще раз.",
    ),
    CancelStep(
        stateToReturn = UserStates.WAITING_FOR_PROJECT_NAME,
        clearDraft = { draft -> draft.copy(projectName = null) },
        stepName = "название проекта",
        prompt = "Введите название проекта.",
    ),
    CancelStep(
        stateToReturn = UserStates.WAITING_FOR_PURPOSE,
        clearDraft = { draft -> draft.copy(purpose = null) },
        stepName = "назначение",
        prompt = "Введите назначение проекта.",
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
        response.text = NOTHING_TO_CANCEL_TEXT
        response.replyMarkup = ReplyKeyboardRemove(true)
        return
    }

    // Если мы на самом первом шаге, то "шаг назад" делать некуда.
    if (state.stepIndex == UserStates.WAITING_FOR_PHONE.stepIndex) {
        response.text = "ℹ️ Вы на первом шаге. Отправьте номер телефона или нажмите \"Отправить контакт\"."
        response.replyMarkup = phoneKeyboard()
        return
    }

    val returnIndex = state.stepIndex - 1

    // Шаг, на который возвращаемся, определяется автоматически: "на 1 меньше".
    // Важно: при откате мы очищаем поле черновика, которое относится к этому шагу.
    val back = cancelSteps.getOrNull(returnIndex)
    if (back == null) {
        // На всякий случай: если конфигурация шагов изменилась, не падаем.
        response.text = NOTHING_TO_CANCEL_TEXT
        response.replyMarkup = ReplyKeyboardRemove(true)
        return
    }

    val draft = drafts[chatId] ?: SurveyDraft()
    drafts[chatId] = back.clearDraft(draft)
    userStates[chatId] = back.stateToReturn
    response.text = "⬅️ Ок, вернулись к шагу <b>${back.stepName}</b>. ${back.prompt}"
    response.replyMarkup = replyMarkupForState(back.stateToReturn)
}
