package io.github.suqi8.opatch.hook.corepatch

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences

class ReturnConstant(
    private val prefs: XSharedPreferences,
    private val prefsKey: String,
    private val value: Any?
) : XC_MethodHook() {
    @Throws(Throwable::class)
    override fun beforeHookedMethod(param: MethodHookParam) {
        prefs.reload()
        if (prefs.getBoolean(prefsKey, true)) {
            param.result = value
        }
    }
}
