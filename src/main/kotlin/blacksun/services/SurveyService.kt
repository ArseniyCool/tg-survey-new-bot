package blacksun.services

import jakarta.inject.Singleton
import jakarta.inject.Inject
import org.telegram.telegrambots.meta.api.objects.Update //основной контейнер события
import org.telegram.telegrambots.meta.api.objects.Message //формирование ответа


@Singleton //одиночный класс, класс-сервис, глобальный сервис

/* UPDATE
Пользователь написал сообщение
Пользователь нажал кнопку
Пользователь отправил команду
Произошло любое действие с ботом
➡ Telegram формирует объект Update и отправляет его твоему серверу.
*/
class SurveyService {
    fun handle(update: Update): Message {

        val chatId = update.message.chatId //id пользователя
        val text = update.message.text.lowercase() //сообщение пользователя

        val response = Message() //объект ответа

        when (text) {
            "/start" -> response.text = "Добро пожаловать!"

            "/ping" -> response.text = "Pong!"

            else -> response.text = "Я не понимаю эту команду"
        }

        return response
    }
}