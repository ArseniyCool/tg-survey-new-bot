package telegram

// CODEX: Align application root package to telegram.* so Micronaut scans all beans in this namespace.
import io.micronaut.runtime.Micronaut

fun main() {
    Micronaut.build().start()
}