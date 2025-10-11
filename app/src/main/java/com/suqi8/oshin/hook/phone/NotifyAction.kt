package com.suqi8.oshin.hook.phone

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.suqi8.oshin.R
import com.suqi8.oshin.hook.com.android.phone.CopyCodeReceiver.Companion.createIntent

/**
 * 一个具体的操作类，负责创建并显示“新验证码”通知。
 */
class NotifyAction(
    pluginContext: Context?,
    phoneContext: Context?,
    smsMsg: SmsMsg?
) : CallableAction(pluginContext, phoneContext, smsMsg) {

    /**
     * 当此操作被调度执行时，这是入口点。
     */
    override fun action(): Bundle? {
        // 检查用户是否在设置中开启了“显示通知”功能
        if (showCodeNotification) {
            return mSmsMsg?.let { showCodeNotification(it) } // 如果开启了，就执行显示通知的操作
        }
        return null
    }

    /**
     * 创建并显示通知的核心逻辑。
     * @return 如果设置了自动清除，则返回包含通知ID和延迟时间的Bundle。
     */
    @SuppressLint("UnspecifiedImmutableFlag", "NotificationPermission", "LaunchActivityFromNotification")
    private fun showCodeNotification(smsMsg: SmsMsg): Bundle? {
        // 获取系统的通知管理器
        val manager =
            mPhoneContext?.let { it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager? }
        if (manager == null) {
            return null
        }

        // 准备通知的标题和内容
        val company = smsMsg.company
        val smsCode = smsMsg.smsCode
        val title = if (TextUtils.isEmpty(company)) smsMsg.sender else company
        val content = mPluginContext?.getString(R.string.code_notification_content, smsCode)

        // 使用短信的哈希码作为通知ID，确保唯一性
        val notificationId = smsMsg.hashCode()

        // 创建一个点击通知时要执行的意图（在这里是复制验证码）
        val copyCodeIntent = createIntent(smsCode)
        // 创建一个 PendingIntent，它封装了 copyCodeIntent，以便在另一个进程（系统UI）中安全地执行
        // 这里的 FLAG_MUTABLE 和 FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT 是为了兼容新版安卓的安全策略
        val contentIntent = PendingIntent.getBroadcast(
            mPhoneContext,
            0,
            copyCodeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
        )

        // 使用 NotificationCompat.Builder 来构建通知
        val notification = mPluginContext?.let { context ->
            NotificationCompat.Builder(context, NotificationConst.CHANNEL_ID_SMSCODE_NOTIFICATION).apply {
                //setSmallIcon(R.drawable.icon) // 设置小图标
                setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.icon)) // 设置大图标
                setWhen(System.currentTimeMillis()) // 设置时间戳
                setContentTitle(title) // 设置标题
                setContentText(content) // 设置内容
                setContentIntent(contentIntent) // 设置点击事件
                setAutoCancel(true) // 点击后自动清除通知
                setColor(ContextCompat.getColor(context, R.color.colorPrimaryAccent)) // 设置颜色
                setGroup(NotificationConst.GROUP_KEY_SMSCODE_NOTIFICATION) // 将通知分组
            }.build()
        }

        // 显示通知
        manager.notify(notificationId, notification)

        // 如果用户开启了“自动清除通知”功能
        /*if (XSPUtils.autoCancelCodeNotification(xsp)) {
            // 获取用户设置的通知保留时间
            val retentionTime = XSPUtils.getNotificationRetentionTime(xsp) * 1000L
            // 将通知ID和保留时间打包返回，以便后续的清除任务使用
            val bundle = Bundle()
            bundle.putLong(NOTIFY_RETENTION_TIME, retentionTime)
            bundle.putInt(NOTIFY_ID, notificationId)
            return bundle
        }*/
        return null
    }

    companion object { // 定义常量
        // 用于在 Bundle 中传递通知保留时间的 Key
        const val NOTIFY_RETENTION_TIME: String = "notify_retention_time"
        // 用于在 Bundle 中传递通知ID的 Key
        const val NOTIFY_ID: String = "notify_id"
    }
}
