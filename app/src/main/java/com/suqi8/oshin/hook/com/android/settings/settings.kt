package com.suqi8.oshin.hook.com.android.settings

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.os.Environment
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import java.io.File

class settings: YukiBaseHooker() {
    @SuppressLint("PrivateApi")
    override fun onHook() {
        loadApp(hooker = feature())
        loadApp(hooker = Accessibility())
        loadApp(name = "com.android.settings") {
            if (prefs("settings").getString("custom_display_model", "") != "") {
                "com.oplus.settings.feature.deviceinfo.controller.OplusDeviceModelPreferenceController".toClass().apply {
                    method{
                        name = "getStatusText"
                        emptyParam()
                        returnType = StringClass
                    }.hook {
                        replaceTo(prefs("settings").getString("custom_display_model", ""))
                    }
                }
            }
            if (prefs("settings").getBoolean("enable_ota_card_bg", false)) {
                "com.oplus.settings.widget.preference.AboutDeviceOtaUpdatePreference".toClass().apply {
                    method {
                        name = "onBindViewHolder"
                        param("androidx.preference.PreferenceViewHolder")
                        returnType = UnitType
                    }.hook {
                        after {
                            val holder = args[0] as Any
                            val itemView = holder.javaClass.getField("itemView").get(holder) as RelativeLayout
                            File("${Environment.getExternalStorageDirectory()}/.OShin/settings/ota_card.png").takeIf { it.exists() }?.let { file ->
                                val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(file))
                                RoundedBitmapDrawableFactory.create(appContext!!.resources, bitmap).apply {
                                    cornerRadius = prefs("settings").getFloat("ota_corner_radius", 0f)
                                }.also { itemView.post { itemView.background = it } }
                            }
                        }
                    }
                }
            }
            if (prefs("settings").getBoolean("force_show_nfc_security_chip", false)) {
                "com.oplus.settings.feature.deviceinfo.DeviceInfoUtils".toClass().apply {
                    method {
                        name = "isSupportNfcEse"
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        before {
                            result = true
                        }
                    }
                }
            }
            /*"com.android.settings.applications.appinfo.AppInfoDashboardFragment".toClass().apply {
                method {
                    name = "onCreateOptionsMenu"
                    param("android.view.Menu", "android.view.MenuInflater")
                    returnType = UnitType
                }.hook {
                    after {
                        val menu = args[0] as Menu
                        menu.add(0, 3, 0, "aaa").setShowAsAction(0)
                        args[0] = menu
                    }
                }
            }*/
        }
    }
}

