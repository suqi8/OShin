package com.suqi8.oshin.features.launcher

import com.suqi8.oshin.R
import com.suqi8.oshin.models.Action
import com.suqi8.oshin.models.AppName
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.Dropdown
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

object launcher {
    val definition = PageDefinition(
        category = "launcher",
        appList = listOf("com.android.launcher"),
        title = AppName("com.android.launcher"),
        items = listOf(
            CardDefinition(
                items = listOf(
                    Action(
                        title = StringResource(id = R.string.recent_tasks),
                        route = "launcher\\recent_task"
                    )
                )
            ),
            CardDefinition(
                items = listOf(
                    Slider(
                        title = StringResource(R.string.desktop_icon_and_text_size_multiplier),
                        summary = R.string.icon_size_limit_note,
                        key = "icon_text",
                        defaultValue = 1.0f,
                        unit = "x",
                        valueRange = 0f..2f,
                        decimalPlaces = 1
                    ),
                    Switch(
                        title = StringResource(R.string.force_enable_fold_mode),
                        key = "force_enable_fold_mode"
                    ),
                    Dropdown(
                        title = StringResource(R.string.fold_mode),
                        key = "fold_mode",
                        optionsRes = R.array.fold_mode,
                        condition = SimpleCondition(
                            dependencyKey = "force_enable_fold_mode"
                        )
                    ),
                    Switch(
                        title = StringResource(R.string.force_enable_fold_dock),
                        key = "force_enable_fold_dock"
                    ),
                    Slider(
                        title = StringResource(R.string.adjust_dock_transparency),
                        key = "dock_transparency",
                        defaultValue = 1f,
                        unit = "f",
                        valueRange = 0f..10f,
                        decimalPlaces = 2
                    ),
                    Switch(
                        title = StringResource(R.string.force_enable_dock_blur),
                        summary = R.string.force_enable_dock_blur_undevice,
                        key = "force_enable_dock_blur"
                    ),
                    Slider(
                        title = StringResource(R.string.set_anim_level),
                        key = "set_anim_level",
                        valueRange = 0f..4f,
                        defaultValue = -1f,
                        decimalPlaces = 0
                    )
                )
            )
        )
    )
}
