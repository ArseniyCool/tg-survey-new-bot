package telegram.commands.handling

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.commands.phoneKeyboard
import telegram.commands.previousStepFor
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.text.BotMessages
import java.time.Instant

/**
 * РћР±СЂР°Р±РѕС‚РєР° РєРѕРјР°РЅРґС‹ /cancel.
 */
internal fun handleCancelCommand(session: UserSession, response: MutableBotReply): UserSession? {
    val state = session.state

    if (state == null) {
        response.text = BotMessages.NOTHING_TO_CANCEL
        response.replyMarkup = ReplyKeyboardRemove(true)
        return null
    }

    if (state.stepIndex == UserStates.WAITING_FOR_PHONE.stepIndex) {
        response.text = BotMessages.CANCEL_AT_FIRST_STEP
        response.replyMarkup = phoneKeyboard()
        return null
    }

    val back = previousStepFor(state)
    if (back == null) {
        response.text = BotMessages.NOTHING_TO_CANCEL
        response.replyMarkup = ReplyKeyboardRemove(true)
        return null
    }

    response.text = BotMessages.cancelBack(back.stepName, back.prompt)
    response.replyMarkup = back.replyMarkup

    return back
        .clearOnReturn(session)
        .copy(state = back.state, updatedAt = Instant.now())
}


