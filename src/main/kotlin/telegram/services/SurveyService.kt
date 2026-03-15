package telegram.services

import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.commands.handleGlobalCommands
import telegram.commands.handleStatesCommands
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.model.SurveyDraft

@Singleton
class SurveyService {
    val userStates: MutableMap<Long, UserStates> = ConcurrentHashMap()
    val drafts: MutableMap<Long, SurveyDraft> = ConcurrentHashMap()

    fun handle(update: Update): Message {
        val incoming = update.message
        val incomingText = incoming?.text
        if (incoming == null || incomingText == null) {
            val fallback = Message()
            fallback.text = Answers.DONT_UNDERSTAND.text
            return fallback
        }

        val chatId = incoming.chatId
        val rawText = incomingText.trim()
        val normalizedText = rawText.lowercase()
        val toUserMessage = Message()

        // 1) Global commands: compare in normalized form, so /StArT works.
        if (handleGlobalCommands(normalizedText, chatId, userStates, drafts, toUserMessage))
            return toUserMessage

        // 2) State-driven input: keep original casing for project/purpose.
        if (handleStatesCommands(rawText, chatId, userStates, drafts, toUserMessage))
            return toUserMessage

        // 3) Fallback
        toUserMessage.text = Answers.DONT_UNDERSTAND.text
        return toUserMessage
    }
}