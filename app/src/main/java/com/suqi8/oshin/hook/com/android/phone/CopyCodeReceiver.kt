package com.suqi8.oshin.hook.com.android.phone

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.suqi8.oshin.BuildConfig
import com.suqi8.oshin.R

/**
 * 一个广播接收器，负责处理通知栏上的“复制验证码”点击事件。
 */
class CopyCodeReceiver : BroadcastReceiver() {
    /**
     * 使用 Holder 模式实现懒加载的单例，确保全局只有一个接收器实例。
     */
    private object CopyCodeReceiverHolder {
        @SuppressLint("StaticFieldLeak")
        val INSTANCE = CopyCodeReceiver()
    }

    // 缓存本模块的上下文，用于获取资源
    private var mPluginContext: Context? = null

    /**
     * 接收到广播时执行的核心方法。
     */
    override fun onReceive(phoneContext: Context, intent: Intent) {
        val action = intent.action
        // 只处理我们自己定义的“复制验证码”广播
        if (ACTION_COPY_CODE == action) {
            // 从广播的 Intent 中获取验证码
            val smsCode = intent.getStringExtra(EXTRA_KEY_CODE)
            // 将验证码复制到剪贴板
            copyToClipboard(phoneContext, smsCode)

            // 显示一个“已复制”的提示
            val pluginContext = createSmsCodeAppContext(phoneContext)
            showToast(pluginContext, phoneContext, smsCode)
        }
    }

    fun copyToClipboard(context: Context, text: String?) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (cm == null) return
        val clipData = ClipData.newPlainText("Copy text", text)
        cm.setPrimaryClip(clipData)
    }

    /**
     * 创建并缓存本模块的上下文，用于获取字符串等资源。
     */
    private fun createSmsCodeAppContext(phoneContext: Context): Context? {
        if (mPluginContext == null) {
            mPluginContext = phoneContext.createPackageContext(
                BuildConfig.APPLICATION_ID,
                Context.CONTEXT_IGNORE_SECURITY
            )
        }
        return mPluginContext
    }

    /**
     * 显示 Toast 提示。
     */
    private fun showToast(pluginContext: Context?, phoneContext: Context?, smsCode: String?) {
        if (pluginContext != null && phoneContext != null) {
            // 从模块的资源中获取提示文本，并显示
            val text = pluginContext.getString(R.string.prompt_sms_code_copied, smsCode)
            Toast.makeText(phoneContext, text, Toast.LENGTH_LONG).show()
        }
    }

    companion object { // 定义常量和静态辅助方法
        // 自定义广播 Action，确保唯一性
        private const val ACTION_COPY_CODE = BuildConfig.APPLICATION_ID + ".ACTION_COPY_CODE"
        // Intent 中传递验证码的 Key
        private const val EXTRA_KEY_CODE = "extra_key_code"

        /**
         * 获取接收器的单例实例。
         */
        fun newInstance(): CopyCodeReceiver {
            return CopyCodeReceiverHolder.INSTANCE
        }

        /**
         * 创建一个能触发本接收器的 Intent。
         */
        @JvmStatic
        fun createIntent(smsCode: String?): Intent {
            val intent = Intent(ACTION_COPY_CODE)
            intent.putExtra(EXTRA_KEY_CODE, smsCode)
            return intent
        }

        /**
         * 将此接收器动态注册到系统中。
         */
        fun registerMe(context: Context) {
            val receiver: CopyCodeReceiver = newInstance()
            val filter = IntentFilter()
            filter.addAction(ACTION_COPY_CODE) // 设置只接收指定 Action 的广播
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED // 设置为非导出，更安全
            )
        }
    }
}
