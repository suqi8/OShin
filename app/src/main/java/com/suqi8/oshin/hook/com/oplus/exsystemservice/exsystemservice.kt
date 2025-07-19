package com.suqi8.oshin.hook.com.oplus.exsystemservice

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class exsystemservice: YukiBaseHooker() {
    override fun onHook() {
        loadHooker(RemoveSystemTamperWarning())
    }
}
