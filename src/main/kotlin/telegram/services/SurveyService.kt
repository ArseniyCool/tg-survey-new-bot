package telegram.services

import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.commands.handleGlobalCommands
import telegram.commands.handleStatesCommands
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.model.BotReply
import telegram.model.MutableBotReply
import telegram.model.SurveyDraft
import telegram.persistence.SurveySubmissionWriter

@Singleton
class SurveyService(
    private val submissionWriter: SurveySubmissionWriter,
) {
    val userStates: MutableMap<Long, UserStates> = ConcurrentHashMap()
    val drafts: MutableMap<Long, SurveyDraft> = ConcurrentHashMap()

    fun handle(update: Update): BotReply {
        val incoming = update.message
        if (incoming == null) {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val chatId = incoming.chatId
        val contactPhone = incoming.contact?.phoneNumber
        val incomingText = incoming.text

        // We support either plain text messages or contact sharing (phone).
        if (incomingText == null && contactPhone == null) {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val rawText = (incomingText ?: contactPhone ?: return BotReply(text = Answers.DONT_UNDERSTAND.text)).trim()
        val normalizedText = rawText.lowercase()
        val toUser = MutableBotReply()

        // 1) Global commands: compare in normalized form, so /StArT works.
        if (handleGlobalCommands(normalizedText, chatId, userStates, drafts, toUser)) {
            return toUser.toImmutable()
        }

        // 2) State-driven input: keep original casing for project/purpose.
        if (handleStatesCommands(rawText, chatId, userStates, drafts, toUser) { id, completed ->
                submissionWriter.write(id, completed)
            }
        ) {
            return toUser.toImmutable()
        }

        // 3) Fallback
        toUser.text = Answers.DONT_UNDERSTAND.text
        return toUser.toImmutable()
    }
}

