package telegram.commands

/**
 * Клавиатуры (reply markup) для Telegram: кнопки, которые бот может показывать пользователю.
 */

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

internal fun phoneKeyboard(): ReplyKeyboardMarkup {
    val button = KeyboardButton("Отправить контакт")
    button.requestContact = true

    val row = KeyboardRow()
    row.add(button)

    return ReplyKeyboardMarkup(listOf(row)).apply {
        resizeKeyboard = true
        oneTimeKeyboard = true
        selective = true
    }
}

