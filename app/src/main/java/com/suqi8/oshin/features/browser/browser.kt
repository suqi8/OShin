package com.suqi8.oshin.features.browser

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object browser {
    val definition = PageDefinition(
        // 1. 定义整个页面的共享信息
        category = "browser", // 所有 Switch 项将使用这个 category
        appList = listOf("com.heytap.browser"),
        title = AppName("com.heytap.browser"),
        items = listOf(
            CardDefinition(
                titleRes = R.string.weather_detail,
                items = listOf(
                    Switch(
                        title = StringResource(R.string.remove_weather_injected_ads),
                        key = "remove_weather_injected_ads",
                    ),
                    Switch(
                        title = StringResource(R.string.remove_weather_search_box),
                        key = "remove_weather_search_box",
                    )
                )
            )
        )
    )
}
