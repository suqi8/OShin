package com.suqi8.oshin.hook.phone

import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage

object SmsMessageUtils {
    fun fromIntent(intent: Intent): Array<SmsMessage>? {
        return Telephony.Sms.Intents.getMessagesFromIntent(intent)
    }

    fun getMessageBody(messageParts: Array<SmsMessage>): String {
        return messageParts.joinToString("") { it.displayMessageBody }
    }
}
