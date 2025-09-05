package com.suqi8.oshin.hook.com.oplus.padconnect

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class padconnect: YukiBaseHooker() {
    override fun onHook() {
        loadHooker(BypassSameAccountUnlockCheck())
    }
}
