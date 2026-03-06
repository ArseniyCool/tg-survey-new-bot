package telegram.enums

enum class Answers(val text: String) {
    WELCOME("Добро пожаловать!\n\nПожалуйста, отправьте номер телефона."),
    PONG("Понг!"),
    DONT_UNDERSTAND("Я не понимаю эту команду"),
    NUMBER_SAVED("Телефон сохранен! Введите название команды!"),
    INCORRECT_NUMBER("Введен неправильный номер. Повторите попытку."),
    PROJECT_SAVED("Название проекта сохранено. Спасибо!")
}