package com.suqi8.oshin.hook.phone

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle

/**
 * 一个具体的操作类，负责执行“自动清除通知”的逻辑。
 */
class CancelNotifyAction(
    pluginContext: Context?,
    phoneContext: Context?,
    smsMsg: SmsMsg?
) : CallableAction(pluginContext, phoneContext, smsMsg) {
    // 保存要清除的通知的 ID
    private var mNotificationId: Int = NOTIFICATION_NONE

    /**
     * 设置此任务要清除的通知 ID。
     */
    fun setNotificationId(notificationId: Int) {
        mNotificationId = notificationId
    }

    /**
     * 当此操作被调度执行时，这是入口点。
     */
    override fun action(): Bundle? {
        cancelNotification()
        return null
    }

    /**
     * 执行清除通知的具体操作。
     */
    private fun cancelNotification() {
        // 确保我们有一个有效的通知 ID
        if (mNotificationId != NOTIFICATION_NONE) {
            // 获取系统的通知管理器
            val manager =
                mPhoneContext?.let { it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager? }

            manager?.cancel(mNotificationId)
        }
    }

    companion object { // 定义常量
        // 一个无效的通知 ID，用作默认值
        private const val NOTIFICATION_NONE = -0xff
    }
}
