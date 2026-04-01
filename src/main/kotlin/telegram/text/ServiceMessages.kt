package telegram.text

/**
 * РЎР»СѓР¶РµР±РЅС‹Рµ СЃРѕРѕР±С‰РµРЅРёСЏ РїСЂРёР»РѕР¶РµРЅРёСЏ.
 */
object ServiceMessages {
    const val EMPTY_TOKEN_SKIP_COMMANDS_REGISTRATION_LOG: String =
        "telegram.token РїСѓСЃС‚РѕР№; СЂРµРіРёСЃС‚СЂР°С†РёСЋ РјРµРЅСЋ РєРѕРјР°РЅРґ РїСЂРѕРїСѓСЃРєР°РµРј"

    const val COMMANDS_REGISTRATION_SUCCESS_LOG: String =
        "РњРµРЅСЋ РєРѕРјР°РЅРґ Telegram СѓСЃРїРµС€РЅРѕ Р·Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°РЅРѕ"

    const val COMMANDS_REGISTRATION_FAILED_LOG: String =
        "РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°С‚СЊ РјРµРЅСЋ РєРѕРјР°РЅРґ Telegram: status={} body={}"

    const val COMMANDS_REGISTRATION_EXCEPTION_LOG: String =
        "РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°С‚СЊ РјРµРЅСЋ РєРѕРјР°РЅРґ Telegram (РёРіРЅРѕСЂРёСЂСѓРµРј)"

    const val SECRET_MISCONFIGURED_LOG: String =
        "Webhook secret token РЅРµ РЅР°СЃС‚СЂРѕРµРЅ: Р·Р°РґР°Р№С‚Рµ TELEGRAM_WEBHOOK_SECRET РїРµСЂРµРґ Р·Р°РїСѓСЃРєРѕРј РїСЂРёР»РѕР¶РµРЅРёСЏ"

    const val INVALID_SECRET_LOG: String =
        "РћС‚РєР»РѕРЅРµРЅ Р·Р°РїСЂРѕСЃ Рє webhook СЃ РЅРµРІР°Р»РёРґРЅС‹Рј secret token. remote-header-present={} path-secret-present={}"

    const val DUPLICATE_UPDATE_LOG: String =
        "РџРѕРІС‚РѕСЂРЅС‹Р№ update_id={} РїСЂРѕРёРіРЅРѕСЂРёСЂРѕРІР°РЅ"

    const val PROCESSING_FAILED_LOG: String =
        "РћС€РёР±РєР° РїСЂРё РѕР±СЂР°Р±РѕС‚РєРµ РІС…РѕРґСЏС‰РµРіРѕ webhook-РѕР±РЅРѕРІР»РµРЅРёСЏ РѕС‚ Telegram"

    const val PROCESSING_FAILED_REPLY: String =
        "вљ пёЏ РџСЂРѕРёР·РѕС€Р»Р° РѕС€РёР±РєР°. РџРѕРїСЂРѕР±СѓР№С‚Рµ РїРѕР·Р¶Рµ."
}

