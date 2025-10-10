package com.suqi8.oshin.features.template

import com.suqi8.oshin.R
import com.suqi8.oshin.models.Action
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource

object templateMain {
    val definition = PageDefinition(
        category = "template",
        appList = listOf("com.template.main"),
        title = AppName("com.template.main"),
        // 2. 定义页面的卡片列表
        items = listOf(
            // --- 第一个 Card ---
            CardDefinition(
                items = listOf(
                    Action(
                        title = StringResource(R.string.app_name),
                        route = "template"
                    )
                )
            )
        )
    )
}
