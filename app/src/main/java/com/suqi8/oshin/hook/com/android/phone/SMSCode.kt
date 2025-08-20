package com.suqi8.oshin.hook.com.android.phone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.provider.Telephony
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.suqi8.oshin.BuildConfig.APPLICATION_ID
import com.suqi8.oshin.R

var smsRule = "验证码|校验码|检验码|确认码|激活码|动态码|安全码|验证代码|校验代码|检验代码|激活代码|确认代码|动态代码|安全代码|登入码|认证码|识别码|短信口令|动态密码|交易码|上网密码|随机码|动态口令|驗證碼|校驗碼|檢驗碼|確認碼|激活碼|動態碼|驗證代碼|校驗代碼|檢驗代碼|確認代碼|激活代碼|動態代碼|登入碼|認證碼|識別碼|Code|code|CODE|Код|код|КОД|Пароль|пароль|ПАРОЛЬ|Kod|kod|KOD|Ma|Mã|OTP"
var showCodeToast = false
class SMSCode: YukiBaseHooker() {
    // "电话"应用的上下文，用于创建通知、注册广播等
    private var mPhoneContext: Context? = null

    // 本模块的上下文，用于获取模块自身的资源（如字符串）
    private var mPluginContext: Context? = null

    private val pluginContext: Context?
        get() {
            if (mPluginContext == null) {
                try {
                    mPluginContext = mPhoneContext!!.createPackageContext(
                        APPLICATION_ID,
                        Context.CONTEXT_IGNORE_SECURITY
                    )
                } catch (e: Exception) {}
            }
            return mPluginContext
        }

    override fun onHook() {
        loadApp(name = "com.android.phone") {
            if (!prefs("phone").getBoolean("sms_verification_code", false)) return

            smsRule = prefs("phone").getString("SMSCodeRule", smsRule)
            showCodeToast = prefs("phone").getBoolean("showCodeToast", false)
            hookConstructor(this) // Hook 构造方法，用于初始化
            hookDispatchIntent(this) // Hook 短信分发方法，用于拦截短信
        }
    }

    fun hookConstructor(param: PackageParam) = param.apply {
        "com.android.internal.telephony.InboundSmsHandler".toClass().resolve().constructor {  }.hookAll {
            after {
                val context = args[1] as Context?
                if (mPhoneContext == null) { // 确保只初始化一次
                    mPhoneContext = context
                    // 创建本模块的上下文
                    mPluginContext = mPhoneContext!!.createPackageContext(
                        APPLICATION_ID,
                        Context.CONTEXT_IGNORE_SECURITY
                    )
                    val channelName = pluginContext?.getString(R.string.channel_name_smscode_notification)
                    createNotificationChannel(mPhoneContext!!,
                        "oshin_smscode", channelName, NotificationManager.IMPORTANCE_HIGH)
                    CopyCodeReceiver.registerMe(mPhoneContext!!)
                }
            }
        }
    }

    private fun hookDispatchIntent(param: PackageParam) = param.apply {
        val inboundSmsHandlerClass = "com.android.internal.telephony.InboundSmsHandler".toClass()

        // 通过遍历的方式模糊查找 dispatchIntent 方法，提高兼容性
        var exactMethod: String? = null
        val DISPATCH_INTENT = "dispatchIntent"
        var receiverIndex = 0 // 记录 BroadcastReceiver 参数的位置
        for (method in inboundSmsHandlerClass.declaredMethods) {
            if (DISPATCH_INTENT == method.name) {
                exactMethod = method.name
                // 顺便找到 BroadcastReceiver 类型参数的索引
                val parameterTypes = method.parameterTypes
                for (i in parameterTypes.indices) {
                    if (BroadcastReceiver::class.java.isAssignableFrom(parameterTypes[i]!!)) {
                        receiverIndex = i
                    }
                }
                break
            }
        }

        if (exactMethod == null) return@apply

        // 对找到的方法进行 Hook
        inboundSmsHandlerClass.resolve().firstMethod {
            name = exactMethod
        }.hook {
            before {
                val intent = args[0] as Intent
                // 只关心收到的短信（SMS_DELIVER_ACTION）
                if (Telephony.Sms.Intents.SMS_DELIVER_ACTION != intent.action) return@before

                // 使用 CodeWorker 来解析短信，提取验证码
                val parseResult = CodeWorker(pluginContext, mPhoneContext, intent).parse()
                if (parseResult != null) { // 如果成功解析出验证码
                    if (parseResult) { // 如果配置了要拦截此短信
                        // 从系统数据库中删除这条短信，并通知系统处理完毕
                        deleteRawTableAndSendMessage(instance, args[receiverIndex])
                        // 阻止原始方法继续执行，从而实现拦截效果
                        resultNull()
                    }
                }
            }
        }
    }

    private fun deleteRawTableAndSendMessage(inboundSmsHandler: Any, smsReceiver: Any?) {
        val token = Binder.clearCallingIdentity() // 临时获取系统权限
        try {
            deleteFromRawTable(inboundSmsHandler, smsReceiver) // 从数据库删除
        } catch (e: Throwable) {
            YLog.error("删除短信失败: ", e)
        } finally {
            Binder.restoreCallingIdentity(token) // 恢复原始身份
        }
        inboundSmsHandler.asResolver().firstMethod {
            name = "sendMessage"
            parameters(Int::class.java)
            superclass()
        }.invoke(3)
    }

    @Throws(ReflectiveOperationException::class)
    private fun deleteFromRawTable(inboundSmsHandler: Any, smsReceiver: Any?) {
        // 通过反射获取删除短信所需的条件和参数
        val deleteWhere = smsReceiver?.asResolver()?.firstField { name = "mDeleteWhere" }
            ?.get() as String?
        @Suppress("UNCHECKED_CAST")
        val deleteWhereArgs = smsReceiver?.asResolver()?.firstField { name = "mDeleteWhereArgs" }
            ?.get() as Array<String>?
        val MARK_DELETED = 2
        inboundSmsHandler.asResolver().firstMethod {
            name = "deleteFromRawTable"
            parameters(String::class, Array<String>::class.java, Int::class)
            superclass()
        }.invoke(deleteWhere, deleteWhereArgs, MARK_DELETED)
    }

    fun createNotificationChannel(
        context: Context, channelId: String?,
        channelName: String?, importance: Int
    ) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        manager?.createNotificationChannel(channel)
    }
}
