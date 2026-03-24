package telegram.format

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EscapeHtmlTest {

    @Test
    fun `escape html should replace telegram html special chars`() {
        val escaped = escapeHtml("<b>Tom & 'Jerry'</b>")

        assertEquals("&lt;b&gt;Tom &amp; &#39;Jerry&#39;&lt;/b&gt;", escaped)
    }
}
