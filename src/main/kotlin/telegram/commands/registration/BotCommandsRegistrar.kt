package telegram.commands.registration

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.event.ApplicationStartupEvent
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import telegram.text.ServiceMessages
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Р РµРіРёСЃС‚СЂР°С†РёСЏ РјРµРЅСЋ РєРѕРјР°РЅРґ Р±РѕС‚Р° РїСЂРё СЃС‚Р°СЂС‚Рµ РїСЂРёР»РѕР¶РµРЅРёСЏ.
 */
@Singleton
@Requires(property = "telegram.register-commands", value = "true")
class BotCommandsRegistrar(
    @Property(name = "telegram.token") private val token: String,
    private val botCommandsCatalog: BotCommandsCatalog,
) : ApplicationEventListener<ApplicationStartupEvent> {

    private val log = LoggerFactory.getLogger(BotCommandsRegistrar::class.java)

    override fun onApplicationEvent(event: ApplicationStartupEvent) {
        if (token.isBlank()) {
            log.warn(ServiceMessages.EMPTY_TOKEN_SKIP_COMMANDS_REGISTRATION_LOG)
            return
        }

        val url = "https://api.telegram.org/bot$token/setMyCommands"

        try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(botCommandsCatalog.setMyCommandsPayload()))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() in 200..299) {
                log.info(ServiceMessages.COMMANDS_REGISTRATION_SUCCESS_LOG)
            } else {
                log.warn(
                    ServiceMessages.COMMANDS_REGISTRATION_FAILED_LOG,
                    response.statusCode(),
                    response.body()
                )
            }
        } catch (e: Exception) {
            log.warn(ServiceMessages.COMMANDS_REGISTRATION_EXCEPTION_LOG, e)
        }
    }
}


