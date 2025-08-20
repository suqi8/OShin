package com.suqi8.oshin.hook.com.android.phone

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.suqi8.oshin.R

/**
 * 一个具体的操作类，负责显示一个包含验证码的 Toast 提示。
 */
class ToastAction(
    pluginContext: Context?, // 本模块的上下文
    phoneContext: Context?,  // "电话"应用的上下文
    smsMsg: SmsMsg?          // 包含验证码信息的对象
) : RunnableAction(pluginContext, phoneContext, smsMsg) {

    /**
     * 当此操作被调度执行时，这是入口点。
     */
    override fun action(): Bundle? {
        // 检查用户是否在设置中开启了“显示Toast”功能
        if (showCodeToast) {
            showCodeToast() // 如果开启了，就显示 Toast
        }
        return null
    }

    /**
     * 执行显示 Toast 的具体逻辑。
     */
    private fun showCodeToast() {
        // 从本模块的资源中加载提示文本，并将验证码填入
        val text = mSmsMsg?.let { mPluginContext?.getString(R.string.code_notification_content, it.smsCode) }
        // 在"电话"应用的上下文中创建并显示 Toast
        if (mPhoneContext != null) {
            Toast.makeText(mPhoneContext, text, Toast.LENGTH_LONG).show()
        }
    }
}
