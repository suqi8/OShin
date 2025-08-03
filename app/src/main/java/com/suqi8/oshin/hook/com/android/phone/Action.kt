package com.suqi8.oshin.hook.com.android.phone

/**
 * 定义了一个名为 Action 的通用接口。
 * <T> 是一个泛型参数，表示这个动作可以返回任何类型的结果。
 */
interface Action<T> {
    /**
     * 声明所有实现此接口的类，都必须拥有一个名为 action 的方法。
     * 这个方法负责执行具体的操作，并可以返回一个 T 类型的结果，或者返回 null。
     */
    fun action(): T?
}
