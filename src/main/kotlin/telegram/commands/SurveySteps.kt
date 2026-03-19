package telegram.commands

/**
 * Описание шагов опроса в одном месте.
 *
 * Это "wizard"-подход (FSM): у каждого шага есть индекс, подсказка и правило очистки поля при шаге назад (/cancel).
 * Тогда /cancel не содержит повторяющиеся if/when на каждый шаг — он просто делает stepIndex - 1.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.enums.UserStates
import telegram.persistence.UserSession

internal data class SurveyStep(
    val state: UserStates,
    val stepName: String,
    val prompt: String,
    val replyMarkup: ReplyKeyboard?,
    val clearOnReturn: (UserSession) -> UserSession,
)

/**
 * Индекс шага = `UserStates.stepIndex`.
 * ВАЖНО: список должен быть в том же порядке, что и индексы.
 */
internal val surveySteps: List<SurveyStep> = listOf(
    SurveyStep(
        state = UserStates.WAITING_FOR_PHONE,
        stepName = "телефон",
        prompt = "Отправьте номер еще раз.",
        replyMarkup = phoneKeyboard(),
        clearOnReturn = { s -> s.copy(phone = null) },
    ),
    SurveyStep(
        state = UserStates.WAITING_FOR_PROJECT_NAME,
        stepName = "название проекта",
        prompt = "Введите название проекта.",
        replyMarkup = ReplyKeyboardRemove(true),
        clearOnReturn = { s -> s.copy(projectName = null) },
    ),
    SurveyStep(
        state = UserStates.WAITING_FOR_PURPOSE,
        stepName = "назначение",
        prompt = "Введите назначение проекта.",
        replyMarkup = ReplyKeyboardRemove(true),
        clearOnReturn = { s -> s.copy(purpose = null) },
    ),
)

internal fun previousStepFor(state: UserStates): SurveyStep? {
    val prevIndex = state.stepIndex - 1
    return surveySteps.getOrNull(prevIndex)
}

