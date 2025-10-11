package com.suqi8.oshin.hook.phone

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtils {
    @JvmStatic
    fun copyToClipboard(context: Context, text: String?) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (cm == null) {
            return
        }
        val clipData = ClipData.newPlainText("Copy text", text)
        cm.setPrimaryClip(clipData)
    }
}
