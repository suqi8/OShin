package com.suqi8.oshin.hook.com.android.phone

import android.annotation.SuppressLint
import android.hardware.input.InputManager
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import de.robv.android.xposed.XposedHelpers

/**
 * 模拟输入法输入字符的辅助工具类。<br></br>
 * 参考了 com.android.commands.input.Input 的实现。
 */
object InputHelper {
    /**
     * 模拟输入一段指定的文本。
     * @param text 要输入的文本
     * @throws Throwable 如果没有 INJECT_EVENTS 权限会抛出异常
     */
    @Throws(Throwable::class)
    fun sendText(text: String) {
        val source = InputDevice.SOURCE_KEYBOARD // 定义输入源为键盘

        val sb = kotlin.text.StringBuilder(text)

        // 处理特殊转义字符，例如将 %s 转换为空格 (这是模仿了 adb shell input 命令的行为)
        var escapeFlag = false
        var i = 0
        while (i < sb.length) {
            if (escapeFlag) {
                escapeFlag = false
                if (sb[i] == 's') {
                    sb.setCharAt(i, ' ')
                    sb.deleteCharAt(--i)
                }
            }
            if (sb[i] == '%') {
                escapeFlag = true
            }
            i++
        }

        val chars = sb.toString().toCharArray()

        // 获取一个虚拟键盘的按键映射表
        val kcm = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
        // 将字符串转换成一个按键事件的序列 (例如，输入'A'需要 SHIFT_DOWN, A_DOWN, A_UP, SHIFT_UP)
        val events = kcm.getEvents(chars)
        // 遍历并注入每一个按键事件
        for (keyEvent in events) {
            if (source != keyEvent.source) {
                keyEvent.source = source
            }
            injectKeyEvent(keyEvent)
        }
    }

    /**
     * 将单个按键事件注入到系统中。
     */
    @SuppressLint("PrivateApi")
    @Throws(Throwable::class)
    private fun injectKeyEvent(keyEvent: KeyEvent?) {
        // 通过反射获取系统输入管理器 InputManager
        val inputManager =
            XposedHelpers.callStaticMethod(InputManager::class.java, "getInstance") as InputManager?

        // 获取注入模式：等待事件处理完成后再继续，这样可以保证输入的顺序和稳定性
        val INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH =
            XposedHelpers.getStaticIntField(
                InputManager::class.java,
                "INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH"
            )

        val paramTypes =
            arrayOf(KeyEvent::class.java, Int::class.javaPrimitiveType)
        val args = arrayOf(keyEvent, INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH)

        // 调用 InputManager 的内部方法 injectInputEvent 来实现注入
        XposedHelpers.callMethod(inputManager, "injectInputEvent", paramTypes, *args)
    }
}
