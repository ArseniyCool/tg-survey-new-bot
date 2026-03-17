package telegram.commands

/**
 * Обработка пользовательского ввода по шагам опроса (состояния).
 * Здесь выполняются проверки и формируются ответы/квитанция.
 */

import telegram.enums.UserStates
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
import telegram.commands.state.handleWaitingForPhone
import telegram.commands.state.handleWaitingForProjectName
import telegram.commands.state.handleWaitingForPurpose
import kotlin.collections.set

fun handleStatesCommands(
    fromUserMessage: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    drafts: MutableMap<Long, SurveyDraft>,
    toUserMessage: MutableBotReply,
    onCompleted: (Long, SurveyDraft) -> Unit,
): Boolean {

    val state = userStates[chatId] ?: return false

    when (state) {
        UserStates.WAITING_FOR_PHONE -> {
            return handleWaitingForPhone(fromUserMessage, chatId, userStates, drafts, toUserMessage)
        }

        UserStates.WAITING_FOR_PROJECT_NAME -> {
            return handleWaitingForProjectName(fromUserMessage, chatId, userStates, drafts, toUserMessage)
        }

        UserStates.WAITING_FOR_PURPOSE -> {
            return handleWaitingForPurpose(fromUserMessage, chatId, userStates, drafts, toUserMessage, onCompleted)
        }

        UserStates.COMPLETED -> return false
    }
}

