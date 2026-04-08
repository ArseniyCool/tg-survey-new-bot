package telegram

/**
 * Точка входа приложения.
 */

import io.micronaut.runtime.Micronaut
import java.util.Locale

private const val LOGBACK_CONFIG_PROPERTY = "logback.configurationFile"
private const val TEXT_LOG_CONFIG = "logback.xml"
private const val JSON_LOG_CONFIG = "logback-json.xml"

fun main() {
    configureLogging(System.getenv("LOG_FORMAT"))
    Micronaut.build().start()
}

internal fun configureLogging(logFormat: String?) {
    val configName = when (normalizeLogFormat(logFormat)) {
        "json" -> JSON_LOG_CONFIG
        else -> TEXT_LOG_CONFIG
    }

    val configUrl = Thread.currentThread().contextClassLoader.getResource(configName) ?: return
    System.setProperty(LOGBACK_CONFIG_PROPERTY, configUrl.toString())
}

internal fun normalizeLogFormat(logFormat: String?): String {
    return logFormat
        ?.trim()
        ?.lowercase(Locale.ROOT)
        ?.takeIf { it.isNotEmpty() }
        ?: "text"
}
