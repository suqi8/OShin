package com.suqi8.oshin.hook.com.android.phone

import android.content.Intent
import android.telephony.SmsMessage
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.Normalizer

data class SmsMsg(
    @SerializedName("id")
    var id: Long? = null,

    // 发件人
    @Expose
    @SerializedName("sender")
    var sender: String? = null,

    // 短信内容
    @Expose
    @SerializedName("body")
    var body: String? = null,

    // 接收日期
    @Expose
    @SerializedName("date")
    var date: Long = 0,

    // 公司/服务商
    @Expose
    @SerializedName("company")
    var company: String? = null,

    // 验证码
    @Expose
    @SerializedName("code")
    var smsCode: String? = null
) : Serializable {
    companion object {
        fun fromIntent(intent: Intent): SmsMsg? {
            val smsMessageParts: Array<SmsMessage>? = SmsMessageUtils.fromIntent(intent)
            if (smsMessageParts.isNullOrEmpty()) {
                return null
            }
            val sender = smsMessageParts[0].displayOriginatingAddress?.let {
                Normalizer.normalize(it, Normalizer.Form.NFC)
            }
            val body = SmsMessageUtils.getMessageBody(smsMessageParts).let {
                Normalizer.normalize(it, Normalizer.Form.NFC)
            }
            return SmsMsg(sender = sender, body = body)
        }
    }
}
