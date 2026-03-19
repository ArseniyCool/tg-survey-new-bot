package telegram.commands

/**
 * Обработчики глобальных команд (/start, /help, /cancel, /ping).
 * Эти команды работают независимо от текущего шага опроса.
 */

import telegram.enums.Answers
import telegram.enums.Commands
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.enums.UserStates
import telegram.format.escapeHtml
import java.time.Instant

fun handleGlobalCommands(
    text: String,
    session: UserSession,
    response: MutableBotReply,
): HandlingResult {

    when (text) {
        Commands.START.text -> {
            // /start всегда перезапускает опрос с самого начала.
            response.text = Answers.WELCOME.text
            response.replyMarkup = phoneKeyboard()

            return HandlingResult(
                handled = true,
                updatedSession = session.copy(
                    state = UserStates.WAITING_FOR_PHONE,
                    phone = null,
                    projectName = null,
                    purpose = null,
                    updatedAt = Instant.now(),
                )
            )
        }

        Commands.HELP.text -> {
            response.text = Answers.HELP.text
            return HandlingResult(handled = true)
        }

        Commands.CANCEL.text -> {
            // /cancel = шаг назад (отменить предыдущий ответ)
            val updated = handleCancelCommand(session, response)
            return HandlingResult(handled = true, updatedSession = updated)
        }

        Commands.PING.text -> {
            response.text = Answers.PONG.text
            return HandlingResult(handled = true)
        }

        Commands.CHECK.text, Commands.STATUS.text -> {
            response.text = buildCheckText(session)
            return HandlingResult(handled = true)
        }
    }

    return HandlingResult(handled = false)
}

/**
 * Команда /check (алиас /status): показать текущее состояние анкеты из `user_sessions`.
 *
 * Важно: это только отображение. Мы ничего не сохраняем и не меняем в БД.
 */
private fun buildCheckText(session: UserSession): String {
    val state = session.state

    // Если записи нет в БД, SurveyService создаст объект UserSession(chatId=...) "по умолчанию"
    // (state=null и все поля null). Покажем понятный текст, но не будем создавать новую запись.
    val nothingStarted = state == null && session.phone == null && session.projectName == null && session.purpose == null
    if (nothingStarted) {
        return "ℹ️ Анкета еще не начата.\n\nНажмите /start, чтобы начать опрос."
    }

    val phone = session.phone?.let { "<code>${escapeHtml(it)}</code>" } ?: "—"
    val project = session.projectName?.let { "<code>${escapeHtml(it)}</code>" } ?: "—"
    val purpose = session.purpose?.let { "<code>${escapeHtml(it)}</code>" } ?: "—"

    val stepText = when (state) {
        UserStates.WAITING_FOR_PHONE -> "ожидаю <b>телефон</b>"
        UserStates.WAITING_FOR_PROJECT_NAME -> "ожидаю <b>название проекта</b>"
        UserStates.WAITING_FOR_PURPOSE -> "ожидаю <b>назначение</b>"
        UserStates.COMPLETED -> "<b>анкета заполнена</b>"
        null -> "не начата"
    }

    return buildString {
        append("🧾 <b>Состояние анкеты</b>\n")
        append("📌 Шаг: ").append(stepText).append("\n\n")
        append("📱 <b>Телефон:</b> ").append(phone).append("\n")
        append("📦 <b>Проект:</b> ").append(project).append("\n")
        append("🎯 <b>Назначение:</b> ").append(purpose).append("\n\n")
        append("⬅️ Шаг назад: /cancel\n")
        append("🔁 Заполнить заново: /start")
    }
}
