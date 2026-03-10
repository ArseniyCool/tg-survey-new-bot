package telegram.services

import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.commands.handleGlobalCommands
import telegram.commands.handleStatesCommands
import telegram.enums.Answers
import telegram.enums.UserStates

@Singleton
class SurveyService {
    val userStates: MutableMap<Long, UserStates> = ConcurrentHashMap()

    fun handle(update: Update): Message {
        val incoming = update.message
        val incomingText = incoming?.text
        if (incoming == null || incomingText == null) {
            val fallback = Message()
            fallback.text = Answers.DONT_UNDERSTAND.text
            return fallback
        }

        val chatId = incoming.chatId
        val fromUserMessage = incomingText.lowercase()
        val toUserMessage = Message()

        // 1. Global commands
        if (handleGlobalCommands(fromUserMessage, chatId, userStates, toUserMessage)) return toUserMessage

        // 2. State-driven commands
        if (handleStatesCommands(fromUserMessage, chatId, userStates, toUserMessage)) return toUserMessage

        // 3. Fallback
        toUserMessage.text = Answers.DONT_UNDERSTAND.text
        return toUserMessage
    }
}