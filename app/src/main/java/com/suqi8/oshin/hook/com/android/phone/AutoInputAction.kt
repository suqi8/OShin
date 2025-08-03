package com.suqi8.oshin.hook.com.android.phone

import android.content.Context
import android.os.Bundle

/**
 * 一个具体的操作类，负责执行“自动输入验证码”的逻辑。
 */
class AutoInputAction(
    pluginContext: Context?,
    phoneContext: Context?,
    smsMsg: SmsMsg?
) : CallableAction(pluginContext, phoneContext, smsMsg) {

    /**
     * 当此操作被调度执行时，这是入口点。
     */
    override fun action(): Bundle? {
        prepareAutoInputCode(mSmsMsg?.smsCode)
        return null
    }

    /**
     * 准备自动输入，会先检查当前应用是否在黑名单中。
     */
    private fun prepareAutoInputCode(code: String?) {
        code?.let { InputHelper.sendText(it) }
    }
}
