package org.example.telegram.commands

import org.telegram.telegrambots.meta.api.objects.Message
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.validation.isValidPhoneNumber
import kotlin.collections.set


fun handleStatesCommands(
    fromUserMessage: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    toUserMessage: Message
): Boolean {

    val state = userStates[chatId]

    if (state != null) {
        when (state) {

            UserStates.WAITING_FOR_PHONE -> {

                if (!isValidPhoneNumber(fromUserMessage)) {
                    toUserMessage.text = Answers.INCORRECT_NUMBER.text
                    return true
                }

                toUserMessage.text = Answers.NUMBER_SAVED.text
                userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME
            }

            UserStates.WAITING_FOR_PROJECT_NAME -> {
                toUserMessage.text = Answers.PROJECT_SAVED.text
                userStates.remove(chatId)
            }
        }
        return true
    }
    return false
}