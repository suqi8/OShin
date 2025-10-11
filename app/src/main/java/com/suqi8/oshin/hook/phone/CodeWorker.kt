package com.suqi8.oshin.hook.phone

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 验证码处理的核心工作类。
 * 负责接收短信意图，解析验证码，并调度一系列后续操作（如复制、通知、自动输入等）。
 */
class CodeWorker internal constructor(
    private val mPluginContext: Context?, // 本模块的上下文
    private val mPhoneContext: Context?,  // "电话"应用的上下文
    smsIntent: Intent?                  // 包含短信内容的 Intent
) {
    // 包含短信数据的 Intent
    private val mSmsIntent: Intent? = smsIntent

    // 用于在主线程（UI线程）执行操作，如显示Toast
    private val mUIHandler: Handler = Handler(Looper.getMainLooper())

    // 一个单线程的线程池，用于执行后台和延迟任务
    private val mScheduledExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    /**
     * 主要的解析和调度方法
     */
    fun parse(): Boolean? {
        // --- 第一步：异步解析短信，判断是否为验证码 ---
        val smsParseAction = SmsParseAction(mPluginContext, mPhoneContext, null)
        smsParseAction.setSmsIntent(mSmsIntent)
        // 将解析任务提交到线程池，并获取 Future 对象以便等待结果
        val smsParseFuture = mScheduledExecutor.schedule(smsParseAction, 0, TimeUnit.MILLISECONDS)

        val smsMsg: SmsMsg?
        try {
            val parseBundle = smsParseFuture.get() // 等待解析任务完成并获取结果
            if (parseBundle == null) {
                // 如果解析结果为空，说明不是验证码短信，直接退出
                return null
            }

            // 检查是否是重复的短信
            val duplicated = parseBundle.getBoolean(SmsParseAction.SMS_DUPLICATED, false)
            if (duplicated) {
                return buildParseResult() // 如果是重复短信，也直接退出，但需要返回拦截状态
            }

            // 从结果中获取解析出的 SmsMsg 对象
            smsMsg = parseBundle.getParcelable(SmsParseAction.SMS_MSG, SmsMsg::class.java)
        } catch (e: Exception) {
            return null
        }

        // --- 第二步：成功解析出验证码后，调度一系列后续操作 ---

        // 复制验证码到剪贴板 (在UI线程执行)
        mUIHandler.post(CopyToClipboardAction(mPluginContext, mPhoneContext, smsMsg))

        // 显示“已复制”提示 (在UI线程执行)
        mUIHandler.post(ToastAction(mPluginContext, mPhoneContext, smsMsg))

        // 如果开启了自动输入功能
        if (inputCode) {
            val autoInputAction = AutoInputAction(mPluginContext, mPhoneContext, smsMsg)
            val autoInputDelay = 0L * 1000L
            // 延迟指定时间后，执行自动输入任务
            mScheduledExecutor.schedule(autoInputAction, autoInputDelay, TimeUnit.MILLISECONDS)
        }

        // 显示“新验证码”通知
        val notifyAction = NotifyAction(mPluginContext, mPhoneContext, smsMsg)
        val notificationFuture = mScheduledExecutor.schedule(notifyAction, 0, TimeUnit.MILLISECONDS)

        // 延迟3秒后，将短信标为已读或删除
        val operateSmsAction = OperateSmsAction(mPluginContext, mPhoneContext, smsMsg)
        mScheduledExecutor.schedule(operateSmsAction, 3000, TimeUnit.MILLISECONDS)

        // 延迟4秒后，执行清理任务 (如关闭线程池)
        /*val action = KillMeAction(mPluginContext, mPhoneContext, smsMsg, xsp)
        mScheduledExecutor.schedule(action, 4000, TimeUnit.MILLISECONDS)
*/
        // --- 第三步：处理通知的自动清除 ---
        try {
            // 等待通知任务执行完毕，获取通知ID和设置的保留时间
            val bundle = notificationFuture.get()
            if (bundle != null && bundle.containsKey(NotifyAction.NOTIFY_RETENTION_TIME)) {
                val delay = bundle.getLong(NotifyAction.NOTIFY_RETENTION_TIME, 0L)
                val notificationId = bundle.getInt(NotifyAction.NOTIFY_ID, 0)
                // 创建一个清除通知的任务
                val cancelNotifyAction = CancelNotifyAction(mPluginContext, mPhoneContext, smsMsg)
                cancelNotifyAction.setNotificationId(notificationId)

                // 延迟指定时间后，自动清除这条通知
                mScheduledExecutor.schedule(cancelNotifyAction, delay, TimeUnit.MILLISECONDS)
            }
        } catch (e: Exception) {}

        // --- 第四步：返回最终结果 ---
        return buildParseResult()
    }

    /**
     * 构建最终结果，告诉调用者（SmsHandlerHook）是否要拦截原始短信
     */
    private fun buildParseResult(): Boolean {
        return true
    }
}
