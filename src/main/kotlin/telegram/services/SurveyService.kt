package telegram.services


import telegram.enums.Answers
import telegram.enums.UserStates
import jakarta.inject.Singleton
import org.example.telegram.commands.handleGlobalCommands
import org.example.telegram.commands.handleStatesCommands

import org.telegram.telegrambots.meta.api.objects.Update //основной контейнер события
import org.telegram.telegrambots.meta.api.objects.Message //формирование ответа

/* UPDATE
Пользователь написал сообщение
Пользователь нажал кнопку
Пользователь отправил команду
Произошло любое действие с ботом
➡ Telegram формирует объект Update и отправляет его твоему серверу.
*/
@Singleton //одиночный класс, класс-сервис, глобальный сервис
class SurveyService {
    val userStates = mutableMapOf<Long, UserStates>()


    fun handle(update: Update): Message {

        val chatId = update.message.chatId //id пользователя
        val fromUserMessage = update.message.text.lowercase() //сообщение пользователя
        val toUserMessage = Message() //объект ответа

        // 1. Глобальные команды
        if (handleGlobalCommands(fromUserMessage, chatId, userStates, toUserMessage)) return toUserMessage

        // 2. Команды в состоянии приема данных
        if (handleStatesCommands(fromUserMessage, chatId, userStates, toUserMessage)) return toUserMessage

        // 3. Fallback
        toUserMessage.text = Answers.DONT_UNDERSTAND.text
        return toUserMessage
    }
}