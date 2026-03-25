package telegram.commands

/**
 * Логика команды /cancel.
 *
 * /cancel означает "шаг назад": отменяем последний заполненный шаг
 * и возвращаем пользователя на предыдущий этап опроса.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.text.Messages
import java.time.Instant

internal fun handleCancelCommand(session: UserSession, response: MutableBotReply): UserSession? {
    val state = session.state

    if (state == null) {
        response.text = Messages.NOTHING_TO_CANCEL
        response.replyMarkup = ReplyKeyboardRemove(true)
        return null
    }

    // Если пользователь уже на первом шаге, откатываться некуда.
    if (state.stepIndex == UserStates.WAITING_FOR_PHONE.stepIndex) {
        response.text = Messages.CANCEL_AT_FIRST_STEP
        response.replyMarkup = phoneKeyboard()
        return null
    }

    val back = previousStepFor(state)
    if (back == null) {
        response.text = Messages.NOTHING_TO_CANCEL
        response.replyMarkup = ReplyKeyboardRemove(true)
        return null
    }

    response.text = Messages.cancelBack(back.stepName, back.prompt)
    response.replyMarkup = back.replyMarkup

    return back
        .clearOnReturn(session)
        .copy(state = back.state, updatedAt = Instant.now())
}
