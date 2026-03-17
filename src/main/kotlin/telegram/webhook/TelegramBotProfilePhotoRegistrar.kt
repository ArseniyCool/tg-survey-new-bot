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
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

@Singleton
@Requires(property = "telegram.set-profile-photo", value = "true")
class TelegramBotProfilePhotoRegistrar(
    @Property(name = "telegram.token") private val token: String,
    @Property(name = "telegram.profile-photo-path") private val photoPath: String,
    @Property(name = "telegram.remove-profile-photo") private val removePhoto: Boolean,
) : ApplicationEventListener<ApplicationStartupEvent> {

    private val log = LoggerFactory.getLogger(TelegramBotProfilePhotoRegistrar::class.java)

    override fun onApplicationEvent(event: ApplicationStartupEvent) {
        if (token.isBlank()) {
            log.warn("telegram.token is blank; skip profile photo setup")
            return
        }

        if (removePhoto) {
            removeProfilePhoto()
            return
        }

        if (photoPath.isBlank()) {
            log.warn("telegram.profile-photo-path is blank; skip profile photo setup")
            return
        }

        val path = Path.of(photoPath)
        if (!Files.exists(path)) {
            log.warn("Profile photo file does not exist: {}", path.toAbsolutePath())
            return
        }

        try {
            setProfilePhoto(path)
        } catch (e: Exception) {
            // Do not fail the app startup if Telegram API/file access is unavailable.
            log.warn("Failed to set Telegram profile photo (ignored)", e)
        }
    }

    private fun removeProfilePhoto() {
        val url = "https://api.telegram.org/bot$token/removeMyProfilePhoto"

        try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() in 200..299) {
                log.info("Telegram profile photo removed successfully")
            } else {
                log.warn("Failed to remove Telegram profile photo: status={} body={}", response.statusCode(), response.body())
            }
        } catch (e: Exception) {
            log.warn("Failed to remove Telegram profile photo (ignored)", e)
        }
    }

    private fun setProfilePhoto(photoFile: Path) {
        val fileBytes = Files.readAllBytes(photoFile)

        // Telegram expects an InputProfilePhoto object that references an attached file.
        // We'll attach a single JPEG/PNG file under the name "profile_photo".
        val attachName = "profile_photo"
        val photoParamJson = """
            {"type":"static","photo":"attach://$attachName"}
        """.trimIndent()

        val boundary = "----tg-bot-boundary-${System.currentTimeMillis()}"
        val body = buildMultipart(boundary) {
            addText("photo", photoParamJson)
            addFile(
                name = attachName,
                filename = photoFile.fileName.toString(),
                contentType = guessImageContentType(photoFile),
                bytes = fileBytes,
            )
        }

        val url = "https://api.telegram.org/bot$token/setMyProfilePhoto"

        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "multipart/form-data; boundary=$boundary")
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() in 200..299) {
            log.info("Telegram profile photo set successfully")
        } else {
            log.warn("Failed to set Telegram profile photo: status={} body={}", response.statusCode(), response.body())
        }
    }

    private fun guessImageContentType(path: Path): String {
        val name = path.fileName.toString().lowercase()
        return when {
            name.endsWith(".png") -> "image/png"
            name.endsWith(".jpg") || name.endsWith(".jpeg") -> "image/jpeg"
            else -> "application/octet-stream"
        }
    }

    private class MultipartBuilder(private val boundary: String) {
        private val out = ArrayList<ByteArray>()

        fun addText(name: String, value: String) {
            val header = "--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"$name\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n\r\n"
            out.add(header.toByteArray(StandardCharsets.UTF_8))
            out.add(value.toByteArray(StandardCharsets.UTF_8))
            out.add("\r\n".toByteArray(StandardCharsets.UTF_8))
        }

        fun addFile(name: String, filename: String, contentType: String, bytes: ByteArray) {
            val header = "--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"$name\"; filename=\"$filename\"\r\n" +
                "Content-Type: $contentType\r\n\r\n"
            out.add(header.toByteArray(StandardCharsets.UTF_8))
            out.add(bytes)
            out.add("\r\n".toByteArray(StandardCharsets.UTF_8))
        }

        fun build(): ByteArray {
            out.add("--$boundary--\r\n".toByteArray(StandardCharsets.UTF_8))
            val size = out.sumOf { it.size }
            val merged = ByteArray(size)
            var pos = 0
            for (part in out) {
                System.arraycopy(part, 0, merged, pos, part.size)
                pos += part.size
            }
            return merged
        }
    }

    private fun buildMultipart(boundary: String, block: MultipartBuilder.() -> Unit): ByteArray {
        val b = MultipartBuilder(boundary)
        b.block()
        return b.build()
    }
}
