package telegram.webhook

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
        // Configure the bot "slash commands" menu in Telegram UI.
        // Telegram expects commands without leading '/'.
        if (token.isBlank()) {
            log.warn("telegram.token is blank; skip commands registration")
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
                log.info("Telegram commands registered successfully")
            } else {
                log.warn("Failed to register Telegram commands: status={} body={}", response.statusCode(), response.body())
            }
        } catch (e: Exception) {
            // Do not fail the app startup if Telegram API is unreachable.
            log.warn("Failed to register Telegram commands (ignored)", e)
        }
    }
}
