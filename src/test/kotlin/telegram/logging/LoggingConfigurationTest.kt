package telegram.logging

import ch.qos.logback.classic.LoggerContext
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class LoggingConfigurationTest {

    @Test
    fun `slf4j backend should be logback`() {
        val loggerFactory = LoggerFactory.getILoggerFactory()
        assertTrue(loggerFactory is LoggerContext)
    }

    @Test
    fun `text and json logback configs should exist on classpath`() {
        assertNotNull(javaClass.classLoader.getResource("logback.xml"))
        assertNotNull(javaClass.classLoader.getResource("logback-json.xml"))
    }
}
