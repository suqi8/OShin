package com.suqi8.oshin.hook.phone

import android.content.Context
import android.os.Bundle
import java.util.concurrent.Callable

/**
 * 一个通用的抽象基类，结合了自定义的 Action 接口和 Java 的 Callable 接口。
 * 它为所有需要在后台线程执行并可能返回结果的任务提供了一个统一的模板。
 */
abstract class CallableAction(
    // 将所有子类都可能用到的通用数据和上下文作为受保护成员，方便子类直接访问
    protected var mPluginContext: Context?,
    protected var mPhoneContext: Context?,
    protected var mSmsMsg: SmsMsg?
) : Action<Bundle?>, Callable<Bundle?> { // 实现自定义的 Action 接口和 Java 的 Callable 接口

    /**
     * 实现了 Callable 接口的 call 方法。
     * 这是任务在线程池中被执行的入口。
     */
    override fun call(): Bundle? {
        return try {
            action()
        } catch (t: Throwable) {
            null
        }
    }
}
