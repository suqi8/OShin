package com.suqi8.oshin.hook.com.android.phone

import android.content.Context

/**
 * 在 [CallableAction] 的基础上，实现了 [Runnable] 接口。
 *
 * 此类的主要目的是将一个可调用、有返回值的任务 ([CallableAction]) 封装成一个
 * 可以在 UI 主线程执行，但不需要处理其返回值的任务 ([Runnable])。
 *
 * @param pluginContext 模块上下文。
 * @param phoneContext 电话应用上下文。
 * @param smsMsg 解析后的短信对象。
 * @param xsp Xposed 共享设置。
 */
abstract class RunnableAction(
    pluginContext: Context?,
    phoneContext: Context?,
    smsMsg: SmsMsg?
) : CallableAction(pluginContext, phoneContext, smsMsg), Runnable {

    /**
     * 实现了 [Runnable] 接口的 `run` 方法。
     *
     * 当此任务被提交到 Handler（例如，UI 主线程的 Handler）时，此方法会被调用。
     * 它直接调用父类 [CallableAction] 的 [call] 方法，从而复用统一的
     * 任务执行和异常处理逻辑，同时忽略其返回值。
     *
     * 此方法被标记为 `final`，以确保子类不会意外地覆盖它，而是应该去实现 [action] 方法。
     */
    final override fun run() {
        // 直接调用 call()，其返回值在这里被忽略。
        // 父类的 call() 方法已经处理了 action() 的调用和异常捕获。
        call()
    }
}
