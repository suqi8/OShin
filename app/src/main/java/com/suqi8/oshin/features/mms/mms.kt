package com.suqi8.oshin.features.mms

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object mms {
    val definition = PageDefinition(
        category = "mms",
        appList = listOf("com.android.mms"),
        title = AppName("com.android.mms"),
        // 2. 定义页面的卡片列表
        items = listOf(
            // --- 第一个 Card ---
            CardDefinition(
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_message_ads),
                        key = "remove_message_ads",
                    )
                )
            )
        )
    )
}
