package com.suqi8.oshin.hook.com.android.phone

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker


class phone: YukiBaseHooker() {
    override fun onHook() {
        loadApp(hooker = SMSCode())
    }
}
