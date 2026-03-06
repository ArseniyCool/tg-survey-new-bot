package blacksun.services

import jakarta.inject.Singleton
import org.telegram.telegrambots.meta.api.objects.Update //основной контейнер события
import org.telegram.telegrambots.meta.api.objects.Message //формирование ответа

enum class Answers(val text: String) {
    WELCOME("Добро пожаловать!\n\nПожалуйста, отправьте номер телефона."),
    PONG("Понг!"),
    DONTUNDERSTAND("Я не понимаю эту команду"),
    NUMBERSAVED("Телефон сохранен! Введите название команды!"),
    INCORRECTNUMBER("Введен неправильный номер. Повторите попытку."),
    PROJECTSAVED("Название проекта сохранено. Спасибо!")
}

enum class UserStates {
    WAITING_FOR_PHONE,
    WAITING_FOR_PROJECT_NAME
}

@Singleton //одиночный класс, класс-сервис, глобальный сервис

/* UPDATE
Пользователь написал сообщение
Пользователь нажал кнопку
Пользователь отправил команду
Произошло любое действие с ботом
➡ Telegram формирует объект Update и отправляет его твоему серверу.
*/

class SurveyService {
    val userStates = mutableMapOf<Long, UserStates>()
    private val phoneRegex = Regex("^(\\+7|8)\\d{10}$") // +7XXXXXXXXXX или 8XXXXXXXXXX

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneRegex.matches(phoneNumber)
    }
    fun handle(update: Update): Message {

        val chatId = update.message.chatId //id пользователя
        val text = update.message.text.lowercase() //сообщение пользователя

        val response = Message() //объект ответа

        // 1. Глобальные команды
        when (text) {

            "/start" -> {
                userStates[chatId] = UserStates.WAITING_FOR_PHONE
                response.text = Answers.WELCOME.text
                return response
            }

            "/ping" -> {
                response.text = "Понг!"
                return response
            }
        }
        // 2. Команды в состоянии приема данных
        val state = userStates[chatId]

        if (state != null) {
            when (state) {

                UserStates.WAITING_FOR_PHONE -> {

                    if (!isValidPhoneNumber(text)) {
                        response.text = Answers.INCORRECTNUMBER.text
                        return response
                    }

                    response.text = Answers.NUMBERSAVED.text
                    userStates[chatId] = UserStates.WAITING_FOR_PROJECT_NAME
                }

                UserStates.WAITING_FOR_PROJECT_NAME -> {
                    response.text = Answers.PROJECTSAVED.text
                    userStates.remove(chatId)
                }
            }

            return response
        }
        // 3. Fallback
        response.text = Answers.DONTUNDERSTAND.text
        return response
    }
}