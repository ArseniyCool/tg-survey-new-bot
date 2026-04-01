package telegram.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ServiceMessagesTest {

    @Test
    fun `should keep command registration logs in one place`() {
        assertEquals(
            "telegram.token РїСѓСЃС‚РѕР№; СЂРµРіРёСЃС‚СЂР°С†РёСЋ РјРµРЅСЋ РєРѕРјР°РЅРґ РїСЂРѕРїСѓСЃРєР°РµРј",
            ServiceMessages.EMPTY_TOKEN_SKIP_COMMANDS_REGISTRATION_LOG
        )
        assertEquals(
            "РњРµРЅСЋ РєРѕРјР°РЅРґ Telegram СѓСЃРїРµС€РЅРѕ Р·Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°РЅРѕ",
            ServiceMessages.COMMANDS_REGISTRATION_SUCCESS_LOG
        )
        assertEquals(
            "РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°С‚СЊ РјРµРЅСЋ РєРѕРјР°РЅРґ Telegram (РёРіРЅРѕСЂРёСЂСѓРµРј)",
            ServiceMessages.COMMANDS_REGISTRATION_EXCEPTION_LOG
        )
    }

    @Test
    fun `should keep configured processing failed reply in one place`() {
        assertEquals("вљ пёЏ РџСЂРѕРёР·РѕС€Р»Р° РѕС€РёР±РєР°. РџРѕРїСЂРѕР±СѓР№С‚Рµ РїРѕР·Р¶Рµ.", ServiceMessages.PROCESSING_FAILED_REPLY)
    }

    @Test
    fun `should keep invalid secret log template with both placeholders`() {
        assertTrue(ServiceMessages.INVALID_SECRET_LOG.contains("remote-header-present={}"))
        assertTrue(ServiceMessages.INVALID_SECRET_LOG.contains("path-secret-present={}"))
    }
}

