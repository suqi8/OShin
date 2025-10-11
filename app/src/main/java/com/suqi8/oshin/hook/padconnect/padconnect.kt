package com.suqi8.oshin.hook.padconnect

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class padconnect: YukiBaseHooker() {
    override fun onHook() {
        loadHooker(BypassSameAccountUnlockCheck())
    }
}
