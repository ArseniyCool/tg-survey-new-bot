package telegram.enums

enum class Answers(val text: String) {
    WELCOME(
        "👋 <b>Добро пожаловать!</b>\n\n" +
            "📱 Отправьте <b>номер телефона</b> текстом или нажмите кнопку <b>\"Отправить контакт\"</b>."
    ),
    HELP(
        "ℹ️ <b>Команды</b>:\n" +
            "• /start — начать или перезапустить опрос\n" +
            "• /cancel — шаг назад (отменить предыдущий ответ)\n" +
            "• /ping — проверка связи\n" +
            "• /help — справка\n\n" +
            "📝 <b>Опрос</b>: телефон → название проекта → назначение."
    ),

    PONG("🏓 Понг!"),
    DONT_UNDERSTAND("🤔 Не понял. Напишите /start, чтобы начать, или /help для списка команд."),

    // Prompts for the next step (the handlers prepend a dynamic "saved" line with <code>user input</code>).
    NUMBER_SAVED("Введите <b>название проекта</b>."),
    PROJECT_SAVED("Опишите <b>назначение</b> проекта."),

    INCORRECT_NUMBER(
        "❌ Номер выглядит неверно.\n\n" +
            "Пришлите номер в формате <b>8XXXXXXXXXX</b> или нажмите <b>\"Отправить контакт\"</b>."
    ),

    EMOJI_NOT_ALLOWED("❌ Эмодзи в тексте не допускаются.\n\nВведите текст без эмодзи."),
    PROJECT_NAME_LENGTH_INVALID("❌ Название проекта должно быть от <b>5</b> до <b>30</b> символов."),
    PURPOSE_LENGTH_INVALID("❌ Назначение должно быть от <b>5</b> до <b>100</b> символов."),
}
