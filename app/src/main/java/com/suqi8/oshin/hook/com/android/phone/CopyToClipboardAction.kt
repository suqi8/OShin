package com.suqi8.oshin.hook.com.android.phone

import android.content.Context
import android.os.Bundle

/**
 * 一个具体的操作类，负责执行“复制验证码到剪贴板”的逻辑。
 *
 * @param pluginContext 模块上下文。
 * @param phoneContext 电话应用上下文。
 * @param smsMsg 解析后的短信对象。
 * @param xsp Xposed 共享设置。
 */
class CopyToClipboardAction(
    pluginContext: Context?,
    phoneContext: Context?,
    smsMsg: SmsMsg?
) : RunnableAction(pluginContext, phoneContext, smsMsg) {

    /**
     * 当此操作被调度执行时，这是入口点。
     */
    override fun action(): Bundle? {
        // 首先检查用户是否在设置中开启了“自动复制”功能。
        // 假设 xsp 是从父类 RunnableAction 继承的属性。
        if (copyCode) {
            copyToClipboard()
        }
        return null // 此操作不返回任何结果。
    }

    /**
     * 调用工具类，执行实际的复制操作。
     */
    private fun copyToClipboard() {
        mSmsMsg?.smsCode?.let { code ->
            mPluginContext?.let { ClipboardUtils.copyToClipboard(it, code) }
        }
    }
}
