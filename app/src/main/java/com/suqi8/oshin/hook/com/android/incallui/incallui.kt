package com.suqi8.oshin.hook.com.android.incallui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class incallui: YukiBaseHooker() {
    override fun onHook() {
        loadApp(hooker = call_ringtone())
    }
}
