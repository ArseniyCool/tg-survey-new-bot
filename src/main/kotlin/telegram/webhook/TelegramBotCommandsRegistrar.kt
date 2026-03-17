package telegram.webhook

/**
 * Регистрация меню команд бота (setMyCommands) при старте приложения.
 */

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.event.ApplicationStartupEvent
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Singleton
@Requires(property = "telegram.register-commands", value = "true")
class TelegramBotCommandsRegistrar(
    @Property(name = "telegram.token") private val token: String,
) : ApplicationEventListener<ApplicationStartupEvent> {

    private val log = LoggerFactory.getLogger(TelegramBotCommandsRegistrar::class.java)

    override fun onApplicationEvent(event: ApplicationStartupEvent) {
        // Настраиваем меню "слэш-команд" в интерфейсе Telegram.
        // Telegram ожидает команды без ведущего '/'.
        if (token.isBlank()) {
            log.warn("telegram.token пустой; регистрацию меню команд пропускаем")
            return
        }

        val commandsJson = """
            {
              "commands": [
                {"command": "start",  "description": "Начать / перезапустить"},
                {"command": "help",   "description": "Показать справку"},
                {"command": "cancel", "description": "Шаг назад"},
                {"command": "ping",   "description": "Проверка связи"}
              ]
            }
        """.trimIndent()

        val url = "https://api.telegram.org/bot$token/setMyCommands"

        try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(commandsJson))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() in 200..299) {
                log.info("Меню команд Telegram успешно зарегистрировано")
            } else {
                log.warn("Не удалось зарегистрировать меню команд Telegram: status={} body={}", response.statusCode(), response.body())
            }
        } catch (e: Exception) {
            // Не ломаем старт приложения, если Telegram API недоступен.
            log.warn("Не удалось зарегистрировать меню команд Telegram (игнорируем)", e)
        }
    }
}
