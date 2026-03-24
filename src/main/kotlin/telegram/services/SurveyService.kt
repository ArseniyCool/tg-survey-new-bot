package telegram.services

/**
 * Основная логика опроса: принимает Telegram Update, обрабатывает команды и шаги опроса.
 *
 * Состояние и введенные пользователем данные хранятся в БД в таблице `user_sessions`.
 */

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.commands.handleGlobalCommands
import telegram.commands.handleStatesCommands
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.model.BotReply
import telegram.model.MutableBotReply
import telegram.text.Messages

@Singleton
class SurveyService(
    private val userSessionStore: UserSessionStore,
) {
    private val log = LoggerFactory.getLogger(SurveyService::class.java)

    fun handle(update: Update): BotReply {
        val updateId = runCatching { update.updateId.toLong() }.getOrDefault(-1L)
        val incoming = parseIncomingTelegramMessage(update) ?: run {
            log.debug("event=update_ignored reason=unsupported_payload updateId={}", updateId)
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val chatId = incoming.chatId
        val rawText = incoming.rawText
        val normalizedCommand = incoming.normalizedCommand
        val toUser = MutableBotReply()
        val session = userSessionStore.findOrCreate(chatId)

        log.info(
            "event=update_received updateId={} chatId={} state={} isCommand={}",
            updateId,
            chatId,
            session.state,
            normalizedCommand != null,
        )

        if (normalizedCommand == Commands.FORGET.text) {
            userSessionStore.delete(chatId)
            log.info("event=user_data_deleted updateId={} chatId={}", updateId, chatId)
            toUser.text = Messages.forgetOk()
            return toUser.toImmutable()
        }

        val global = handleGlobalCommands(normalizedCommand ?: rawText.lowercase(), session, toUser)
        if (global.handled) {
            global.updatedSession?.let { updated ->
                userSessionStore.save(updated)
                logStateTransition("global_command_handled", updateId, chatId, session.state?.name, updated.state?.name)
            } ?: log.info(
                "event=global_command_handled updateId={} chatId={} command={}",
                updateId,
                chatId,
                normalizedCommand ?: rawText.lowercase(),
            )

            return toUser.toImmutable()
        }

        if (normalizedCommand != null) {
            log.warn(
                "event=unknown_command updateId={} chatId={} command={}",
                updateId,
                chatId,
                normalizedCommand,
            )
            toUser.text = Messages.unknownCommand(normalizedCommand)
            return toUser.toImmutable()
        }

        val state = handleStatesCommands(rawText, session, toUser)
        if (state.handled) {
            state.updatedSession?.let { updated ->
                userSessionStore.save(updated)
                logStateTransition("state_input_handled", updateId, chatId, session.state?.name, updated.state?.name)
            } ?: log.info(
                "event=state_input_handled updateId={} chatId={} state={} persisted=false",
                updateId,
                chatId,
                session.state,
            )

            return toUser.toImmutable()
        }

        log.info(
            "event=fallback_response updateId={} chatId={} state={}",
            updateId,
            chatId,
            session.state,
        )
        toUser.text = Answers.DONT_UNDERSTAND.text
        return toUser.toImmutable()
    }

    private fun logStateTransition(
        event: String,
        updateId: Long,
        chatId: Long,
        fromState: String?,
        toState: String?,
    ) {
        log.info(
            "event={} updateId={} chatId={} fromState={} toState={}",
            event,
            updateId,
            chatId,
            fromState ?: "null",
            toState ?: "null",
        )
    }
}
