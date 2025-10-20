package com.suqi8.oshin.hook.weather

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class weather: YukiBaseHooker() {
    override fun onHook() {
        loadHooker(RemoveAds())
    }
}
