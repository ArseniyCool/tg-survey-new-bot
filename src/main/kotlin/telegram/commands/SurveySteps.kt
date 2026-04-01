package telegram.commands

/**
 * РћРїРёСЃР°РЅРёРµ С€Р°РіРѕРІ РѕРїСЂРѕСЃР°.
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
 * РџРѕСЂСЏРґРѕРє С€Р°РіРѕРІ СЃРѕРѕС‚РІРµС‚СЃС‚РІСѓРµС‚ `UserStates.stepIndex`.
 */
internal val surveySteps: List<SurveyStep> = listOf(
    SurveyStep(
        state = UserStates.WAITING_FOR_PHONE,
        stepName = "С‚РµР»РµС„РѕРЅ",
        prompt = "РћС‚РїСЂР°РІСЊС‚Рµ РЅРѕРјРµСЂ РµС‰Рµ СЂР°Р·.",
        replyMarkup = phoneKeyboard(),
        clearOnReturn = { s -> s.copy(phone = null) },
    ),
    SurveyStep(
        state = UserStates.WAITING_FOR_PROJECT_NAME,
        stepName = "РЅР°Р·РІР°РЅРёРµ РїСЂРѕРµРєС‚Р°",
        prompt = "Р’РІРµРґРёС‚Рµ РЅР°Р·РІР°РЅРёРµ РїСЂРѕРµРєС‚Р°.",
        replyMarkup = ReplyKeyboardRemove(true),
        clearOnReturn = { s -> s.copy(projectName = null) },
    ),
    SurveyStep(
        state = UserStates.WAITING_FOR_PURPOSE,
        stepName = "РЅР°Р·РЅР°С‡РµРЅРёРµ",
        prompt = "Р’РІРµРґРёС‚Рµ РЅР°Р·РЅР°С‡РµРЅРёРµ РїСЂРѕРµРєС‚Р°.",
        replyMarkup = ReplyKeyboardRemove(true),
        clearOnReturn = { s -> s.copy(purpose = null) },
    ),
)

internal fun previousStepFor(state: UserStates): SurveyStep? {
    val prevIndex = state.stepIndex - 1
    return surveySteps.getOrNull(prevIndex)
}


