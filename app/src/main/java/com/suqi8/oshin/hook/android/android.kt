package com.suqi8.oshin.hook.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class android : YukiBaseHooker() {
    override fun onHook() {
        loadApp(hooker = OplusRootCheck())
        loadApp(hooker = split_screen_multi_window())
        loadApp(hooker = DisablePinVerifyPer72h())
    }
}
