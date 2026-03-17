package com.suqi8.oshin.hook.android

import android.media.AudioAttributes
import android.media.AudioManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.kavaref.KavaRef.Companion.resolve

class IgnoreAudioFocus : YukiBaseHooker() {
    override fun onHook() {
        loadSystem {
            if (prefs("android").getBoolean("IgnoreAudioFocus", false)) {
                "com.android.server.audio.MediaFocusControl".toClass().resolve().apply {
                    firstMethod {
                        name = "requestAudioFocus"
                    }.hook {
                        before {
                            val audioAttrs = args().first().cast<AudioAttributes>()
                                ?: run {
                                    return@before
                                }
                            val usage = audioAttrs.usage
                            // 允许通话、辅助功能和导航提示
                            if (usage != AudioAttributes.USAGE_VOICE_COMMUNICATION &&
                                usage != AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY &&
                                usage != AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE) {
                                result = AudioManager.AUDIOFOCUS_REQUEST_GRANTED
                            }
                        }
                    }
                }
            }
        }
    }
}