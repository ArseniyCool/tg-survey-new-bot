package telegram

/**
 * Точка входа приложения: запускает Micronaut и поднимает HTTP-сервер бота.
 */

import io.micronaut.runtime.Micronaut

fun main() {
    Micronaut.build().start()
}