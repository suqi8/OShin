package com.suqi8.oshin.features.systemui

import com.suqi8.oshin.R
import com.suqi8.oshin.models.AndCondition
import com.suqi8.oshin.models.CardDefinition
import com.suqi8.oshin.models.Dropdown
import com.suqi8.oshin.models.NoEnable
import com.suqi8.oshin.models.Operator
import com.suqi8.oshin.models.PageDefinition
import com.suqi8.oshin.models.SimpleCondition
import com.suqi8.oshin.models.Slider
import com.suqi8.oshin.models.StringResource
import com.suqi8.oshin.models.Switch

/**
 * 状态栏网速指示器配置页面
 *
 * 页面结构:
 * 1. 主开关卡片
 * 2. 基础设置卡片 (样式、字体、对齐)
 * 3. 字体大小设置卡片
 * 4. 显示控制卡片 (慢速隐藏、阈值)
 * 5. 高级设置卡片 (箭头、单位、交换)
 */
object StatusBarWifi {
    val definition = PageDefinition(
        title = StringResource(R.string.network_speed_indicator),
        category = "systemui\\status_bar\\status_bar_wifi",
        appList = listOf("com.android.systemui"),
        items = listOf(
            // ==================== 1. 主开关卡片 ====================
            CardDefinition(
                items = listOf(
                    Switch(
                        key = "status_bar_wifi",
                        title = StringResource(R.string.network_speed_indicator)
                    )
                )
            ),

            // 当主开关关闭时，禁用后续所有设置
            NoEnable(condition = SimpleCondition("status_bar_wifi", requiredValue = false)),

            // ==================== 2. 基础设置卡片 ====================
            CardDefinition(
                titleRes = R.string.basic_settings, // 基础设置
                condition = SimpleCondition("status_bar_wifi", requiredValue = true),
                items = listOf(
                    // 显示样式选择
                    Dropdown(
                        key = "StyleSelectedOption",
                        title = StringResource(R.string.network_speed_style),
                        optionsRes = R.array.network_speed_style_options
                        // 选项: 0=默认(增强原生), 1=上下行分离(完全自定义)
                    ),

                    // 使用系统字体开关
                    Switch(
                        key = "use_system_font",
                        title = StringResource(R.string.use_system_font)
                    ),

                    // 对齐方式选择
                    Dropdown(
                        key = "alignment",
                        title = StringResource(R.string.network_speed_alignment),
                        optionsRes = R.array.network_speed_alignment_options
                        // 选项: 0=居中, 1=左对齐, 3=右对齐
                    )
                )
            ),

            // ==================== 3. 字体大小设置卡片 ====================
            // 3.1 默认样式的字体设置
            CardDefinition(
                titleRes = R.string.font_size_settings, // 字体大小设置
                condition = AndCondition(listOf(
                    SimpleCondition("status_bar_wifi", requiredValue = true),
                    SimpleCondition("StyleSelectedOption", requiredValue = 0), // 默认样式
                    SimpleCondition("use_system_font", requiredValue = false)  // 未使用系统字体
                )),
                items = listOf(
                    // 速度数字字体大小
                    Slider(
                        key = "speed_font_size",
                        title = StringResource(R.string.speed_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f,
                        valueRange = -1f..20f,
                        unit = "sp",
                        decimalPlaces = 0
                        // -1 表示使用系统默认值
                    ),

                    // 单位字体大小
                    Slider(
                        key = "unit_font_size",
                        title = StringResource(R.string.unit_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f,
                        valueRange = -1f..20f,
                        unit = "sp",
                        decimalPlaces = 0
                    )
                )
            ),

            // 3.2 上下行分离样式的字体设置
            CardDefinition(
                titleRes = R.string.font_size_settings, // 字体大小设置
                condition = AndCondition(listOf(
                    SimpleCondition("status_bar_wifi", requiredValue = true),
                    SimpleCondition("StyleSelectedOption", requiredValue = 1), // 上下行分离样式
                    SimpleCondition("use_system_font", requiredValue = false)  // 未使用系统字体
                )),
                items = listOf(
                    // 上传速度字体大小
                    Slider(
                        key = "upload_font_size",
                        title = StringResource(R.string.upload_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f,
                        valueRange = -1f..20f,
                        unit = "sp",
                        decimalPlaces = 0
                    ),

                    // 下载速度字体大小
                    Slider(
                        key = "download_font_size",
                        title = StringResource(R.string.download_font_size),
                        summary = R.string.default_value_hint_negative_one,
                        defaultValue = -1f,
                        valueRange = -1f..20f,
                        unit = "sp",
                        decimalPlaces = 0
                    )
                )
            ),

            // ==================== 4. 显示控制卡片 ====================
            CardDefinition(
                titleRes = R.string.display_control, // 显示控制
                condition = SimpleCondition("status_bar_wifi", requiredValue = true),
                items = listOf(
                    // 慢速阈值设置
                    Slider(
                        key = "slow_speed_threshold",
                        title = StringResource(R.string.slow_speed_threshold),
                        defaultValue = 20f,
                        valueRange = 0f..1024f,
                        unit = "KB/S",
                        decimalPlaces = 0
                        // 说明: 低于此速度时视为慢速
                    ),

                    // 慢速时隐藏
                    Switch(
                        key = "hide_on_slow",
                        title = StringResource(R.string.hide_on_slow)
                        // 说明: 当速度低于阈值时隐藏网速显示
                    ),

                    // 上下行都慢时隐藏 (仅上下行分离样式)
                    Switch(
                        key = "hide_when_both_slow",
                        title = StringResource(R.string.hide_when_both_slow),
                        condition = AndCondition(listOf(
                            SimpleCondition("hide_on_slow", requiredValue = true),
                            SimpleCondition("StyleSelectedOption", requiredValue = 1)
                        ))
                        // 说明: 仅当上传和下载速度都低于阈值时才隐藏
                    )
                )
            ),

            // ==================== 5. 高级设置卡片 (仅上下行分离样式) ====================
            CardDefinition(
                titleRes = R.string.advanced_settings, // 高级设置
                condition = AndCondition(listOf(
                    SimpleCondition("status_bar_wifi", requiredValue = true),
                    SimpleCondition("StyleSelectedOption", requiredValue = 1) // 仅上下行分离样式
                )),
                items = listOf(
                    // === 箭头指示器设置 ===
                    // 箭头样式选择
                    Dropdown(
                        key = "icon_indicator",
                        title = StringResource(R.string.icon_indicator),
                        optionsRes = R.array.icon_indicator_options
                        // 选项: 0=无, 1=三角形(动态), 2=三角形2(动态), 3=将棋符号(动态), 4=简单箭头, 5=双线箭头
                    ),

                    // 箭头位置前置
                    Switch(
                        key = "position_speed_indicator_front",
                        title = StringResource(R.string.position_speed_indicator_front),
                        condition = SimpleCondition(
                            dependencyKey = "icon_indicator",
                            operator = Operator.NOT_EQUALS,
                            requiredValue = 0
                        )
                        // 说明: 将箭头显示在速度数字前面而不是后面
                        // 仅当选择了箭头样式时显示此选项
                    ),

                    // === 单位显示设置 ===
                    // 隐藏数字和单位之间的空格
                    Switch(
                        key = "hide_space",
                        title = StringResource(R.string.hide_space)
                        // 示例: "1.5 MB/s" -> "1.5MB/s"
                    ),

                    // 隐藏单位 (仅显示前缀)
                    Switch(
                        key = "hide_bs",
                        title = StringResource(R.string.hide_bs)
                        // 示例: "1.5 MB/s" -> "1.5 M"
                    ),

                    // 隐藏 "/s" 后缀
                    Switch(
                        key = "hide_per_second",
                        title = StringResource(R.string.hide_per_second),
                        summary = R.string.hide_per_second_summary,
                        condition = SimpleCondition("hide_bs", requiredValue = false)
                        // 示例: "1.5 MB/s" -> "1.5 MB"
                        // 仅当未隐藏单位时显示此选项
                    ),

                    // 使用大写 B (Byte)
                    Switch(
                        key = "use_uppercase_b",
                        title = StringResource(R.string.use_uppercase_b),
                        summary = R.string.use_uppercase_b_summary,
                        condition = SimpleCondition("hide_bs", requiredValue = false)
                        // 示例: "1.5 Mb/s" -> "1.5 MB/s"
                        // 仅当未隐藏单位时显示此选项
                    ),

                    // === 布局设置 ===
                    // 交换上传下载位置
                    Switch(
                        key = "swap_upload_download",
                        title = StringResource(R.string.swap_upload_download)
                        // 说明: 将上传速度和下载速度的显示位置互换
                    )
                )
            )
        )
    )
}
