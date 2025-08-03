package com.suqi8.oshin.hook.com.android.phone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

/**
 * 一个具体的操作类，负责从收到的短信中解析出验证码。
 * 这是整个处理流程的第一步，也是最关键的一步。
 */
class SmsParseAction(
    pluginContext: Context?,
    phoneContext: Context?,
    smsMsg: SmsMsg?
) : CallableAction(pluginContext, phoneContext, smsMsg) {
    // 包含原始短信数据的 Intent
    private var mSmsIntent: Intent? = null

    fun setSmsIntent(smsIntent: Intent?) {
        mSmsIntent = smsIntent
    }

    /**
     * 当此操作被调度执行时，这是入口点。
     */
    override fun action(): Bundle? {
        return parseSmsMsg()
    }

    /**
     * 解析短信的核心逻辑。
     * @return 如果是验证码短信，则返回包含解析结果的 Bundle，否则返回 null。
     */
    private fun parseSmsMsg(): Bundle? {
        // 1. 从 Intent 中提取 SmsMsg 对象，如果为空则提前返回
        val sms = mSmsIntent?.let { SmsMsg.fromIntent(it) } ?: return null

        // 2. 获取发件人和内容，如果为空则提前返回
        val sender = sms.sender
        val body = sms.body
        if (sender == null || body == null) return null

        // 4. 解析验证码，如果解析失败则提前返回
        val smsCode = mPluginContext?.let { SmsCodeUtils.parseSmsCodeIfExists(body) }
        if (TextUtils.isEmpty(smsCode)) {
            return null
        }

        // 5. 使用 apply 更新 SmsMsg 对象的属性，代码更集中
        val timestamp = System.currentTimeMillis()
        sms.apply {
            this.smsCode = smsCode
            this.company = SmsCodeUtils.parseCompany(body)
            this.date = timestamp
        }

        // 6. 处理短信去重逻辑
        var duplicated = false
        /*val prevSmsMsg = EntityStoreManager.loadEntityFromFile(EntityType.PREV_SMS_MSG, SmsMsg::class.java)
        if (prevSmsMsg != null && abs(timestamp - prevSmsMsg.date) <= 15000) {
            if ((sender == prevSmsMsg.sender && smsCode == prevSmsMsg.smsCode) || body == prevSmsMsg.body) {
                duplicated = true
                //("检测到重复短信，忽略")
            }
        }
        // 无论是否重复，都保存当前短信记录以备下次比较
        EntityStoreManager.storeEntityToFile(EntityType.PREV_SMS_MSG, sms)*/

        // 7. 将最终结果打包到 Bundle 中返回
        return Bundle().apply {
            putSerializable(SMS_MSG, sms)
            putBoolean(SMS_DUPLICATED, duplicated)
        }
    }

    companion object { // 定义常量，用于在 Bundle 中传递数据
        const val SMS_MSG: String = "sms_msg"
        const val SMS_DUPLICATED: String = "sms_duplicated"
    }
}
