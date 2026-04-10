package telegram

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainTest {

    @Test
    fun `normalize log format should default to text`() {
        assertEquals("text", normalizeLogFormat(null))
        assertEquals("text", normalizeLogFormat(""))
        assertEquals("text", normalizeLogFormat("   "))
    }

    @Test
    fun `normalize log format should support json and text values`() {
        assertEquals("json", normalizeLogFormat("json"))
        assertEquals("json", normalizeLogFormat(" JSON "))
        assertEquals("text", normalizeLogFormat("text"))
        assertEquals("text", normalizeLogFormat("TEXT"))
    }

    @Test
    fun `normalize log format should keep unknown values for text fallback`() {
        assertEquals("pretty", normalizeLogFormat("pretty"))
    }
}
