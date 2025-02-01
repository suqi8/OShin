package com.suqi8.oshin

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.ui.activity.funlistui.SearchList
import com.suqi8.oshin.ui.activity.funlistui.addline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.text.Collator
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun Main_Function(
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    padding: PaddingValues
) {
    val context = LocalContext.current
    val features = listOf(
        item(title = stringResource(R.string.downgr),
            summary = stringResource(R.string.downgr_summary),
            category = "android\\package_manager_services"),
        item(title = stringResource(R.string.authcreak),
        summary = stringResource(R.string.authcreak_summary),
        category = "android\\package_manager_services"),
        item(
        title = stringResource(R.string.digestCreak),
        summary = stringResource(R.string.digestCreak_summary),
        category = "android\\package_manager_services"),
        item(title = stringResource(R.string.UsePreSig),
        summary = stringResource(R.string.UsePreSig_summary),
        category = "android\\package_manager_services"),
        item(title = stringResource(R.string.enhancedMode),
        summary = stringResource(R.string.enhancedMode_summary),
        category = "android\\package_manager_services"),
        item(title = stringResource(R.string.bypassBlock),
            summary = stringResource(R.string.bypassBlock_summary),
            category = "android\\package_manager_services"),
        item(title = stringResource(R.string.shared_user_title),
        summary = stringResource(R.string.shared_user_summary),
        category = "android\\package_manager_services"),
        item(title = stringResource(R.string.disable_verification_agent_title),
        summary = stringResource(R.string.disable_verification_agent_summary),
        category = "android\\package_manager_services"),
        item(title = stringResource(id = R.string.package_manager_services),
            category = "android\\package_manager_services"),
        item(title = stringResource(id = R.string.oplus_system_services),
            category = "android\\oplus_system_services"),
        item(title = stringResource(R.string.oplus_root_check),
            summary = stringResource(R.string.oplus_root_check_summary),
            category = "android\\oplus_system_services"),
        item(title = stringResource(R.string.desktop_icon_and_text_size_multiplier),
            summary = stringResource(R.string.icon_size_limit_note),
            category = "launcher"),
        item(title = stringResource(R.string.power_consumption_indicator),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.dual_cell),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.absolute_value),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.bold_text),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.alignment),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.update_time),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.font_size),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.dual_row_title),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.first_line_content),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.second_line_content),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.power),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.current),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.voltage),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.temperature_indicator),
        category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.show_cpu_temp_data),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.change_cpu_temp_source),
            summary = stringResource(R.string.enter_thermal_zone_number),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.bold_text),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.alignment),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.update_time),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.font_size),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.dual_row_title),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.first_line_content),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.second_line_content),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.battery_temperature),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(R.string.cpu_temperature),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(id = R.string.status_bar_clock),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(id = R.string.hardware_indicator),
            category = "systemui\\hardware_indicator"),
        item(title = stringResource(id = R.string.status_bar_icon),
            category = "systemui\\statusbar_icon"),
        item(title = stringResource(R.string.hide_status_bar),
        category = "systemui"),
        item(title = stringResource(R.string.enable_all_day_screen_off),
        category = "systemui"),
        item(title = stringResource(R.string.force_trigger_ltpo),
            category = "systemui"),
        item(title = stringResource(R.string.status_bar_clock),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_style),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_size),
            summary = stringResource(R.string.clock_size_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_update_time_title),
            summary = stringResource(R.string.clock_update_time_summary),
            category = "systemui\\status_bar_clock"),
        item(title = "dp To px",
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_top_margin),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_bottom_margin),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_left_margin),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_right_margin),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.show_years_title),
            summary = stringResource(R.string.show_years_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.show_month_title),
            summary = stringResource(R.string.show_month_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.show_day_title),
            summary = stringResource(R.string.show_day_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.show_week_title),
            summary = stringResource(R.string.show_week_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.show_cn_hour_title),
            summary = stringResource(R.string.show_cn_hour_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.showtime_period_title),
            summary = stringResource(R.string.showtime_period_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.show_seconds_title),
            summary = stringResource(R.string.show_seconds_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.show_millisecond_title),
            summary = stringResource(R.string.show_millisecond_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.hide_space_title),
            summary = stringResource(R.string.hide_space_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.dual_row_title),
            summary = stringResource(R.string.dual_row_summary),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.alignment),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_format),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.clock_format_example),
            category = "systemui\\status_bar_clock"),
        item(title = stringResource(R.string.status_bar_icon),
            category = "systemui\\statusbar_icon"),
        item(title = stringResource(R.string.wifi_icon),
            category = "systemui\\statusbar_icon"),
        item(title = stringResource(R.string.wifi_arrow),
            category = "systemui\\statusbar_icon"),
        item(title = stringResource(R.string.force_display_memory),
            category = "launcher\\recent_task"),
        item(title = stringResource(id = R.string.recent_tasks),
            category = "launcher\\recent_task"),
        item(title = stringResource(id = R.string.status_bar_notification),
            category = "systemui\\notification"),
        item(title = stringResource(R.string.remove_developer_options_notification),
            category = "systemui\\notification"),
        item(title = stringResource(R.string.low_battery_fluid_cloud_off),
            category = "battery")
    )
    var miuixSearchValue by remember { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isKeyboardVisible by remember { mutableStateOf(false) }
    DisposableEffect(context) {
        val rootView = (context as MainActivity).window.decorView
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val insets = ViewCompat.getRootWindowInsets(rootView)
            isKeyboardVisible = insets?.isVisible(WindowInsetsCompat.Type.ime()) == true
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    // 过滤符合搜索条件的功能
    val collator = Collator.getInstance(Locale.CHINA)
    val filteredFeatures = features.filter {
        it.title.contains(miuixSearchValue, ignoreCase = true) ||
                it.summary?.contains(miuixSearchValue, ignoreCase = true) ?: false
    }.sortedWith { a, b ->
        collator.compare(a.title, b.title)
    }

    Column(
        modifier = Modifier
            .padding(top = padding.calculateTopPadding())
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            inputField = {
                InputField(
                    query = miuixSearchValue,
                    onQueryChange = { miuixSearchValue = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    label = stringResource(R.string.Search),
                    leadingIcon = {
                        Image(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            imageVector = MiuixIcons.Search,
                            colorFilter = BlendModeColorFilter(
                                MiuixTheme.colorScheme.onSurfaceContainer,
                                BlendMode.SrcIn
                            ),
                            contentDescription = stringResource(R.string.Search)
                        )
                    }
                )
            },
            outsideRightAction = {
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable {
                            expanded = false
                            miuixSearchValue = ""
                        },
                    text = stringResource(R.string.cancel),
                    color = MiuixTheme.colorScheme.primary
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = (if (isKeyboardVisible) 6.dp else 65.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().value.dp), top = 6.dp)
            ) {
                LazyColumn(topAppBarScrollBehavior = topAppBarScrollBehavior) {
                    if (filteredFeatures.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "空空如也~")
                            }
                        }
                    }

                    filteredFeatures.forEachIndexed { index, feature ->
                        item {
                            SearchList(
                                title = highlightMatches(feature.title, miuixSearchValue),
                                summary = feature.summary?.let { highlightMatches(it, miuixSearchValue) },
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    //miuixSearchValue = feature.title
                                    expanded = false
                                    navController.navigate(feature.category)
                                }
                            )
                            if (index < filteredFeatures.size - 1) {
                                addline()
                            }
                        }
                    }
                }
            }
        }

        if (expanded) {
            // 如果 expanded 为 true，则显示搜索结果
        } else {
            // 如果 expanded 为 false，则显示 Card
            LazyColumn(Modifier.fillMaxSize(), topAppBarScrollBehavior = topAppBarScrollBehavior) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        Column {
                            FunctionApp("android", "android", navController)
                            addline()
                            FunctionApp("com.android.systemui", "systemui", navController)
                            addline()
                            FunctionApp("com.android.settings", "settings", navController)
                            addline()
                            FunctionApp("com.android.launcher", "launcher", navController)
                            addline()
                            FunctionApp("com.oplus.battery", "battery", navController)
                        }
                    }
                    Spacer(Modifier.size(65.dp))
                }
            }
        }
    }
}

// 高亮匹配内容的函数
fun highlightMatches(text: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text) // 如果查询为空，则返回原始文本

    val regex = Regex("($query)", RegexOption.IGNORE_CASE) // 匹配查询字符串的正则表达式
    val annotatedStringBuilder = AnnotatedString.Builder()

    var lastIndex = 0
    for (match in regex.findAll(text)) {
        // 添加匹配前的文本
        annotatedStringBuilder.append(text.substring(lastIndex, match.range.first))
        // 添加高亮部分
        annotatedStringBuilder.pushStyle(SpanStyle(color = Color.Red))
        annotatedStringBuilder.append(match.value)
        annotatedStringBuilder.pop()
        lastIndex = match.range.last + 1
    }
    // 添加剩余的文本
    annotatedStringBuilder.append(text.substring(lastIndex))

    return annotatedStringBuilder.toAnnotatedString()
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FunctionApp(packageName: String, activityName: String, navController: NavController) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != "noapp") {
            //val context = LocalContext.current
            //val auto_color = context.prefs("settings").getBoolean("auto_color", true)
            val defaultColor = MiuixTheme.colorScheme.primary
            val dominantColor: MutableState<Color> = remember { mutableStateOf(defaultColor) }
            val isLoading = remember { mutableStateOf(true) }

            LaunchedEffect(icon) {

                withContext(Dispatchers.IO) {
                    //if (auto_color)
                    dominantColor.value = getautocolor(icon)
                    isLoading.value = false
                }
                /*val bitmap = icon.asAndroidBitmap() // 假设 icon 是一个 Bitmap 类型
                withContext(Dispatchers.IO) {
                    if (auto_color) {
                        withContext(Dispatchers.IO) {
                            Palette.from(bitmap).generate { palette ->
                                val colorSwatch = palette?.dominantSwatch
                                if (colorSwatch != null) {
                                    val newColor = Color(colorSwatch.rgb)
                                    dominantColor.value = newColor
                                }
                                isLoading.value = false
                            }
                        }
                    } else {
                        isLoading.value = false
                    }
                }*/
            }

            Row(
                modifier = Modifier
                    .clickable {
                        navController.navigate(activityName)
                    }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading.value) {
                    // 显示加载占位符
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = appName)
                        SmallTitle(text = packageName, insideMargin = PaddingValues(0.dp, 0.dp))
                    }
                } else {
                    Card(
                        color = if (YukiHookAPI.Status.isModuleActive) dominantColor.value else MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                            .drawColoredShadow(
                                if (YukiHookAPI.Status.isModuleActive) dominantColor.value else MaterialTheme.colorScheme.errorContainer,
                                1f,
                                borderRadius = 13.dp,
                                shadowRadius = 7.dp,
                                offsetX = 0.dp,
                                offsetY = 0.dp,
                                roundedRect = false
                            )
                    ) {
                        Image(bitmap = icon, contentDescription = "App Icon", modifier = Modifier.size(45.dp))
                    }
                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                        Text(text = appName)
                        SmallTitle(text = packageName, insideMargin = PaddingValues(0.dp, 0.dp))
                    }
                }
            }
        } else {
            Text(text = "$packageName 没有安装", modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp))
        }
    }
}

suspend fun getautocolor(icon: ImageBitmap): Color {
    return withContext(Dispatchers.IO) {
        val bitmap = icon.asAndroidBitmap()

        // 使用 suspendCoroutine 将回调转换为协程
        suspendCoroutine { continuation ->
            Palette.from(bitmap).generate { palette ->
                val colorSwatch = palette?.dominantSwatch
                if (colorSwatch != null) {
                    // 返回获取到的颜色
                    continuation.resume(Color(colorSwatch.rgb))
                } else {
                    // 如果获取不到颜色，返回默认颜色
                    continuation.resume(Color.White)
                }
            }
        }
    }
}

private data class item(
    val title: String,
    val summary: String? = null,
    val category: String
)
