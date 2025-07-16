package com.suqi8.oshin.hook.com.oplus.notificationmanager

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class NotificationManager: YukiBaseHooker() {
    override fun onHook() {
        loadHooker(NotificationCategoryControl())
    }
}
