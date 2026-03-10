package telegram.commands

// CODEX: Unify packages to telegram.* (commands under telegram.commands).
import org.telegram.telegrambots.meta.api.objects.Message
import telegram.enums.Answers
import telegram.enums.UserStates
import telegram.enums.Commands
import kotlin.collections.set


fun handleGlobalCommands(
    text: String,
    chatId: Long,
    userStates: MutableMap<Long, UserStates>,
    response: Message
): Boolean {

    when (text) {

        Commands.START.text -> {
            userStates[chatId] = UserStates.WAITING_FOR_PHONE
            response.text = Answers.WELCOME.text
            return true
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            return true
        }
    }
    return false
}