package com.suqi8.oshin.application

import androidx.appcompat.app.AppCompatDelegate
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.umeng.commonsdk.UMConfigure
import com.umeng.union.UMUnionSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DefaultApplication : ModuleApplication() {

    override fun onCreate() {
        super.onCreate()
        /**
         * 初始化 MMKV
         * Initialize MMKV
         */
        //MMKV.initialize(this)


        UMConfigure.setLogEnabled(true)
        UMConfigure.preInit(this, "67c7dea68f232a05f127781e", "android")
        UMUnionSdk.init(this)

        /**
         * 跟随系统夜间模式
         * Follow system night mode
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // Your code here.
    }
}
