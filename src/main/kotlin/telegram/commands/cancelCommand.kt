package telegram.commands

/**
 * Логика команды /cancel.
 *
 * В этом проекте /cancel означает "шаг назад": отменить последний введенный ответ и вернуться на предыдущий шаг опроса.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.text.Messages
import java.time.Instant

private data class CancelStep(
    val stateToReturn: UserStates,
    val stepName: String,
    val prompt: String,
    val clearSession: (UserSession) -> UserSession,
)

private fun replyMarkupForState(stateToReturn: UserStates): ReplyKeyboard? =
    if (stateToReturn == UserStates.WAITING_FOR_PHONE) phoneKeyboard() else ReplyKeyboardRemove(true)

/**
 * Шаги для команды /cancel по индексу шага: state.stepIndex - 1.
 *
 * Индекс 0 = "телефон", 1 = "название проекта", 2 = "назначение".
 * COMPLETED имеет stepIndex=3 и возвращает на индекс 2 (назначение).
 */
private val cancelSteps: List<CancelStep> = listOf(
    CancelStep(
        stateToReturn = UserStates.WAITING_FOR_PHONE,
        stepName = "телефон",
        prompt = "Отправьте номер еще раз.",
        clearSession = { session -> session.copy(phone = null) },
    ),
    CancelStep(
        stateToReturn = UserStates.WAITING_FOR_PROJECT_NAME,
        stepName = "название проекта",
        prompt = "Введите название проекта.",
        clearSession = { session -> session.copy(projectName = null) },
    ),
    CancelStep(
        stateToReturn = UserStates.WAITING_FOR_PURPOSE,
        stepName = "назначение",
        prompt = "Введите назначение проекта.",
        clearSession = { session -> session.copy(purpose = null) },
    ),
)

internal fun handleCancelCommand(session: UserSession, response: MutableBotReply): UserSession? {
    val state = session.state

    if (state == null) {
        response.text = Messages.NOTHING_TO_CANCEL
        response.replyMarkup = ReplyKeyboardRemove(true)
        return null
    }

    // Если мы на самом первом шаге, то "шаг назад" делать некуда.
    if (state.stepIndex == UserStates.WAITING_FOR_PHONE.stepIndex) {
        response.text = Messages.CANCEL_AT_FIRST_STEP
        response.replyMarkup = phoneKeyboard()
        return null
    }

    val returnIndex = state.stepIndex - 1
    val back = cancelSteps.getOrNull(returnIndex)
    if (back == null) {
        response.text = Messages.NOTHING_TO_CANCEL
        response.replyMarkup = ReplyKeyboardRemove(true)
        return null
    }

    response.text = Messages.cancelBack(back.stepName, back.prompt)
    response.replyMarkup = replyMarkupForState(back.stateToReturn)

    return back
        .clearSession(session)
        .copy(state = back.stateToReturn, updatedAt = Instant.now())
}

