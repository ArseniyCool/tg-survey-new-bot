package telegram.text

import telegram.enums.UserStates
import telegram.format.escapeHtml
import telegram.persistence.UserSession

/**
 * Централизованные тексты и "шаблоны" сообщений бота.
 *
 * Здесь живут именно динамические сообщения (с подстановками и форматированием HTML),
 * чтобы они не были размазаны по обработчикам команд/состояний.
 */
object Messages {

    fun phoneSaved(phone: String): String {
        val phoneEscaped = escapeHtml(phone)
        return "✅ <b>Ваш телефон</b> <code>$phoneEscaped</code> сохранен."
    }

    fun projectSaved(projectName: String): String {
        val projectEscaped = escapeHtml(projectName)
        return "✅ <b>Ваш проект</b> <code>$projectEscaped</code> сохранен."
    }

    fun receipt(phone: String, projectName: String, purpose: String): String {
        val phoneEscaped = escapeHtml(phone)
        val projectEscaped = escapeHtml(projectName)
        val purposeEscaped = escapeHtml(purpose)

        return "" +
            "🧾 <b>Спасибо за заполнение анкеты!</b>\n" +
            "✅ Все сохранено.\n\n" +
            "📱 <b>Телефон:</b> <code>$phoneEscaped</code>\n" +
            "📦 <b>Проект:</b> <code>$projectEscaped</code>\n" +
            "🎯 <b>Назначение:</b> <code>$purposeEscaped</code>\n\n" +
            "🔁 Хотите заполнить заново?\n" +
            "Нажмите /start\n" +
            "⬅️ Шаг назад: /cancel"
    }

    fun unknownCommand(command: String): String {
        val cmdEscaped = escapeHtml(command)
        return "❓ Команда <code>$cmdEscaped</code> не найдена.\n\nНапишите /help, чтобы увидеть список команд."
    }

    fun checkStatus(session: UserSession): String {
        val state = session.state

        val nothingStarted = state == null &&
            session.phone == null &&
            session.projectName == null &&
            session.purpose == null

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
            append("🔁 Заполнить заново: /start\n")
            append("🗑️ Удалить мои данные: /forget")
        }
    }

    const val NOTHING_TO_CANCEL: String =
        "ℹ️ Сейчас нечего отменять. Нажмите /start, чтобы начать опрос."

    const val CANCEL_AT_FIRST_STEP: String =
        "ℹ️ Вы на первом шаге. Отправьте номер телефона или нажмите \"Отправить контакт\"."

    fun cancelBack(stepName: String, prompt: String): String =
        "⬅️ Ок, вернулись к шагу <b>$stepName</b>. $prompt"

    fun forgetOk(): String =
        "🗑️ <b>Ваши данные удалены.</b>\n\nМожете начать опрос заново: /start"
}

