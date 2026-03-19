package telegram.services

/**
 * Основная логика опроса: принимает Telegram Update, обрабатывает команды и шаги опроса.
 *
 * Состояние и введенные пользователем данные хранятся в БД в одной таблице user_sessions.
 */

import jakarta.inject.Singleton
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.commands.handleGlobalCommands
import telegram.commands.handleStatesCommands
import telegram.enums.Answers
import telegram.enums.Commands
import telegram.format.escapeHtml
import telegram.model.BotReply
import telegram.model.MutableBotReply
import telegram.persistence.UserSession
import telegram.persistence.UserSessionRepository

@Singleton
class SurveyService(
    private val sessions: UserSessionRepository,
) {
    fun handle(update: Update): BotReply {
        val incoming = update.message
        if (incoming == null) {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val chatId = incoming.chatId
        val contactPhone = incoming.contact?.phoneNumber
        val incomingText = incoming.text

        // РџРѕРґРґРµСЂР¶РёРІР°РµРј Р»РёР±Рѕ РѕР±С‹С‡РЅС‹Р№ С‚РµРєСЃС‚, Р»РёР±Рѕ РѕС‚РїСЂР°РІРєСѓ РєРѕРЅС‚Р°РєС‚Р° (С‚РµР»РµС„РѕРЅ).
        if (incomingText == null && contactPhone == null) {
            return BotReply(text = Answers.DONT_UNDERSTAND.text)
        }

        val rawText = (incomingText ?: contactPhone ?: return BotReply(text = Answers.DONT_UNDERSTAND.text)).trim()
        val normalizedText = rawText.lowercase()

        // Telegram (особенно в группах) может присылать команды вида "/start@MyBot",
        // а также команды с параметрами: "/start foo". Для сравнения команд выделяем "чистую" команду.
        val normalizedCommand = if (normalizedText.startsWith("/")) {
            normalizedText
                .split(Regex("\\s+"), limit = 2)[0]
                .substringBefore("@")
        } else {
            normalizedText
        }
        val toUser = MutableBotReply()

        val sessionOpt = sessions.findById(chatId)
        val sessionExists = sessionOpt.isPresent
        val session = sessionOpt.orElse(UserSession(chatId = chatId))

        fun persistSession(updated: UserSession) {
            // Р’ Micronaut Data РґР»СЏ СЃСѓС‰РЅРѕСЃС‚РµР№ СЃ "РЅР°Р·РЅР°С‡Р°РµРјС‹Рј" PK (chat_id) save() РјРѕР¶РµС‚ РїРѕРїС‹С‚Р°С‚СЊСЃСЏ INSERT,
            // РїРѕСЌС‚РѕРјСѓ РїСЂРё РЅР°Р»РёС‡РёРё Р·Р°РїРёСЃРё РґРµР»Р°РµРј UPDATE.
            if (sessionExists) {
                sessions.update(updated)
            } else {
                sessions.save(updated)
            }
        }

        // /forget: СѓРґР°Р»РёС‚СЊ РґР°РЅРЅС‹Рµ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ (СЃРµСЃСЃРёСЏ + РІСЃРµ Р°РЅРєРµС‚С‹) Рё РЅР°С‡Р°С‚СЊ Р·Р°РЅРѕРІРѕ.
        if (normalizedCommand == Commands.FORGET.text) {
            sessions.deleteById(chatId)

            toUser.text =
                "🗑️ <b>Ваши данные удалены.</b>\n\n" +
                    "Можете начать опрос заново: /start"
            return toUser.toImmutable()
        }

        // 1) Р“Р»РѕР±Р°Р»СЊРЅС‹Рµ РєРѕРјР°РЅРґС‹: СЃСЂР°РІРЅРёРІР°РµРј РІ РЅРѕСЂРјР°Р»РёР·РѕРІР°РЅРЅРѕРј РІРёРґРµ, С‡С‚РѕР±С‹ СЂР°Р±РѕС‚Р°Р»Рѕ /StArT Рё С‚.Рї.
        val global = handleGlobalCommands(normalizedCommand, session, toUser)
        if (global.handled) {
            global.updatedSession?.let { persistSession(it) }
            return toUser.toImmutable()
        }

        // Любое сообщение, начинающееся с '/', считаем командой. Если мы здесь — команда неизвестна.
        // Это автоматически запрещает вводить "название проекта" / "назначение", начинающиеся с '/'.
        if (normalizedCommand.startsWith("/")) {
            val cmdEscaped = escapeHtml(normalizedCommand)
            toUser.text =
                "❓ Команда <code>$cmdEscaped</code> не найдена.\n\n" +
                    "Напишите /help, чтобы увидеть список команд."
            return toUser.toImmutable()
        }

        // 2) Р’РІРѕРґ РїРѕ С€Р°РіР°Рј (СЃРѕСЃС‚РѕСЏРЅРёСЏРј): РґР»СЏ РЅР°Р·РІР°РЅРёСЏ/РЅР°Р·РЅР°С‡РµРЅРёСЏ СЃРѕС…СЂР°РЅСЏРµРј РёСЃС…РѕРґРЅС‹Р№ СЂРµРіРёСЃС‚СЂ.
        val state = handleStatesCommands(rawText, session, toUser)
        if (state.handled) {
            state.updatedSession?.let { persistSession(it) }
            return toUser.toImmutable()
        }

        // 3) Р—Р°РїР°СЃРЅРѕР№ РІР°СЂРёР°РЅС‚ (РµСЃР»Рё РЅРёС‡РµРіРѕ РЅРµ РїРѕРґРѕС€Р»Рѕ)
        toUser.text = Answers.DONT_UNDERSTAND.text
        return toUser.toImmutable()
    }
}


