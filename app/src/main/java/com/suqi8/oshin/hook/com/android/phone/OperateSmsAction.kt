package com.suqi8.oshin.hook.com.android.phone

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * 一个具体的操作类，负责将验证码短信删除或标记为已读。
 */
class OperateSmsAction(
    pluginContext: Context?,
    phoneContext: Context?,
    smsMsg: SmsMsg?
) : CallableAction(pluginContext, phoneContext, smsMsg) {
    // 定义一个注解，限定操作类型只能是删除或标记已读
    @IntDef(OP_DELETE, OP_MARK_AS_READ)
    private annotation class SmsOp

    /**
     * 当此操作被调度执行时，这是入口点。
     */
    override fun action(): Bundle? {
        val sender = mSmsMsg?.sender
        val body = mSmsMsg?.body
        // 根据用户的配置，决定是删除短信还是标记为已读
        if (false) {
            body?.let { deleteSms(sender, it) }
        } else if (true) {
            body?.let { markSmsAsRead(sender, it) }
        }
        return null
    }

    /**
     * 将短信标记为已读。
     */
    private fun markSmsAsRead(sender: String?, body: String) {
        YLog.info("正在将短信标记为已读...")
        val result = operateSms(sender, body, OP_MARK_AS_READ)
        if (result) {
            YLog.info("成功将短信标记为已读")
        } else {
            YLog.info("标记短信为已读失败")
        }
    }

    /**
     * 删除短信。
     */
    private fun deleteSms(sender: String?, body: String) {
        YLog.info("正在删除短信...")
        val result = operateSms(sender, body, OP_DELETE)
        if (result) {
            YLog.info("成功删除短信")
        } else {
            YLog.info("删除短信失败")
        }
    }


    /**
     * 根据指定的操作类型（删除或标记已读）来处理短信。
     * 这是核心的数据库操作方法。
     */
    private fun operateSms(sender: String?, body: String, @SmsOp smsOp: Int): Boolean {
        var cursor: Cursor? = null
        try {
            // 检查是否有读写短信的权限
            mPluginContext?.let {
                if (ContextCompat.checkSelfPermission(it, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    YLog.info("没有读写短信的权限")
                    return false
                }
            }
            // 定义要查询的数据库列
            val projection = arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.READ,
                Telephony.Sms.DATE
            )
            // 只查询最近的5条短信，并按时间倒序排列，以提高效率
            val sortOrder = Telephony.Sms.DATE + " desc limit 5"
            val uri = Telephony.Sms.CONTENT_URI
            val resolver = mPluginContext?.contentResolver
            if (resolver != null) {
                cursor = resolver.query(uri, projection, null, null, sortOrder)
            }
            if (cursor == null) {
                YLog.info("数据库查询游标为空")
                return false
            }
            // 遍历查询结果
            while (cursor.moveToNext()) {
                val curAddress = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val curRead = cursor.getInt(cursor.getColumnIndexOrThrow("read"))
                val curBody = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                // 寻找那条发件人相同、内容匹配且未读的短信
                if (curAddress == sender && curRead == 0 && curBody.startsWith(body)) {
                    val smsMessageId = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                    val where = Telephony.Sms._ID + " = ?"
                    val selectionArgs = arrayOf<String?>(smsMessageId)
                    if (smsOp == OP_DELETE) {
                        // 执行删除操作
                        val rows = resolver?.delete(uri, where, selectionArgs)
                        rows?.let { if (it > 0) return true }
                    } else if (smsOp == OP_MARK_AS_READ) {
                        // 执行更新（标记已读）操作
                        val values = ContentValues()
                        values.put(Telephony.Sms.READ, true)
                        val rows = resolver?.update(uri, values, where, selectionArgs)
                        rows?.let { if (it > 0) return true }
                    }
                }
            }
        } catch (e: Exception) {
            YLog.info("操作短信失败: ", e)
        } finally {
            // 确保游标被关闭，防止内存泄漏
            cursor?.close()
        }
        return false
    }

    companion object { // 定义常量
        private const val OP_DELETE = 0 // 删除操作的代号
        private const val OP_MARK_AS_READ = 1 // 标记已读操作的代号
    }
}
