package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.suqi8.oshin.ui.activity.funlistui.SearchList
import com.suqi8.oshin.ui.activity.funlistui.addline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import java.text.Collator
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun features(context: Context) = listOf(
    item(title = context.getString(R.string.downgr),
        summary = context.getString(R.string.downgr_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.authcreak),
        summary = context.getString(R.string.authcreak_summary),
        category = "android\\package_manager_services"),
    item(
        title = context.getString(R.string.digestCreak),
        summary = context.getString(R.string.digestCreak_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.UsePreSig),
        summary = context.getString(R.string.UsePreSig_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.enhancedMode),
        summary = context.getString(R.string.enhancedMode_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.bypassBlock),
        summary = context.getString(R.string.bypassBlock_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.shared_user_title),
        summary = context.getString(R.string.shared_user_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.disable_verification_agent_title),
        summary = context.getString(R.string.disable_verification_agent_summary),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.package_manager_services),
        category = "android\\package_manager_services"),
    item(title = context.getString(R.string.oplus_system_services),
        category = "android\\oplus_system_services"),
    item(title = context.getString(R.string.oplus_root_check),
        summary = context.getString(R.string.oplus_root_check_summary),
        category = "android\\oplus_system_services"),
    item(title = context.getString(R.string.desktop_icon_and_text_size_multiplier),
        summary = context.getString(R.string.icon_size_limit_note),
        category = "launcher"),
    item(title = context.getString(R.string.power_consumption_indicator),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.dual_cell),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.absolute_value),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.bold_text),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.alignment),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.update_time),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.font_size),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.dual_row_title),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.first_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.second_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.power),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.current),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.voltage),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.temperature_indicator),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.show_cpu_temp_data),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.change_cpu_temp_source),
        summary = context.getString(R.string.enter_thermal_zone_number),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.bold_text),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.alignment),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.update_time),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.font_size),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.dual_row_title),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.first_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.second_line_content),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.battery_temperature),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.cpu_temperature),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.status_bar_clock),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.hardware_indicator),
        category = "systemui\\hardware_indicator"),
    item(title = context.getString(R.string.status_bar_icon),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.hide_status_bar),
        category = "systemui"),
    item(title = context.getString(R.string.enable_all_day_screen_off),
        category = "systemui"),
    item(title = context.getString(R.string.force_trigger_ltpo),
        category = "systemui"),
    item(title = context.getString(R.string.status_bar_clock),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_style),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_size),
        summary = context.getString(R.string.clock_size_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_update_time_title),
        summary = context.getString(R.string.clock_update_time_summary),
        category = "systemui\\status_bar_clock"),
    item(title = "dp To px",
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_top_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_bottom_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_left_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_right_margin),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_years_title),
        summary = context.getString(R.string.show_years_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_month_title),
        summary = context.getString(R.string.show_month_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_day_title),
        summary = context.getString(R.string.show_day_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_week_title),
        summary = context.getString(R.string.show_week_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_cn_hour_title),
        summary = context.getString(R.string.show_cn_hour_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.showtime_period_title),
        summary = context.getString(R.string.showtime_period_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_seconds_title),
        summary = context.getString(R.string.show_seconds_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.show_millisecond_title),
        summary = context.getString(R.string.show_millisecond_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.hide_space_title),
        summary = context.getString(R.string.hide_space_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.dual_row_title),
        summary = context.getString(R.string.dual_row_summary),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.alignment),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_format),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.clock_format_example),
        category = "systemui\\status_bar_clock"),
    item(title = context.getString(R.string.status_bar_icon),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.wifi_icon),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.wifi_arrow),
        category = "systemui\\statusbar_icon"),
    item(title = context.getString(R.string.force_display_memory),
        category = "launcher\\recent_task"),
    item(title = context.getString(R.string.recent_tasks),
        category = "launcher\\recent_task"),
    item(title = context.getString(R.string.status_bar_notification),
        category = "systemui\\notification"),
    item(title = context.getString(R.string.remove_developer_options_notification),
        summary = context.getString(R.string.notification_restriction_message),
        category = "systemui\\notification"),
    item(title = context.getString(R.string.low_battery_fluid_cloud_off),
        category = "battery"),
    item(title = context.getString(R.string.remove_and_do_not_disturb_notification),
        summary = context.getString(R.string.notification_restriction_message),
        category = "systemui\\notification")
)

@Composable
fun Main_Function(
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    padding: PaddingValues
) {
    val context = LocalContext.current
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
    val filteredFeatures = features(context).filter {
        it.title.contains(miuixSearchValue, ignoreCase = true) ||
                it.summary?.contains(miuixSearchValue, ignoreCase = true) ?: false
    }.sortedWith { a, b ->
        collator.compare(a.title, b.title)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                .background(Color.Transparent)
                .padding(top = padding.calculateTopPadding()),
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
                    .padding(top = 6.dp, bottom = if (isKeyboardVisible) 0.dp else padding.calculateBottomPadding())
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
                    Spacer(modifier = Modifier.size(68.dp+padding.calculateTopPadding()))
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
                    Spacer(modifier = Modifier.padding(padding.calculateBottomPadding()))
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
            val colorSaver = Saver<Color, List<Float>>(
                save = { listOf(it.red, it.green, it.blue, it.alpha) },
                restore = { Color(it[0], it[1], it[2], it[3]) }
            )
            val defaultColor = MiuixTheme.colorScheme.primary
            val dominantColor: MutableState<Color> = rememberSaveable(stateSaver = colorSaver) { mutableStateOf(defaultColor) }
            val isLoading = rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(icon) {

                if (isLoading.value) {
                    withContext(Dispatchers.IO) {
                        //if (auto_color)
                        dominantColor.value = getautocolor(icon)
                        isLoading.value = false
                    }
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

data class item(
    val title: String,
    val summary: String? = null,
    val category: String
)

@Composable
fun SearchBar(
    inputField: @Composable () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    outsideRightAction: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.zIndex(1f),color = Color.Transparent,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    inputField()
                }
                AnimatedVisibility(
                    visible = expanded
                ) {
                    outsideRightAction?.invoke()
                }
            }

            AnimatedVisibility(
                visible = expanded
            ) {
                content()
            }
        }
    }

    BackHandler(enabled = expanded) {
        onExpandedChange(false)
    }
}

@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    label: String = "",
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    insideMargin: DpSize = DpSize(12.dp, 12.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val paddingModifier = remember(insideMargin, leadingIcon, trailingIcon) {
        if (leadingIcon == null && trailingIcon == null) Modifier.padding(horizontal = insideMargin.width, vertical = insideMargin.height)
        else if (leadingIcon == null) Modifier.padding(start = insideMargin.width).padding(vertical = insideMargin.height)
        else if (trailingIcon == null) Modifier.padding(end = insideMargin.width).padding(vertical = insideMargin.height)
        else Modifier.padding(vertical = insideMargin.height)
    }

    val focused = interactionSource.collectIsFocusedAsState().value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onExpandedChange(true) }
            .semantics {
                onClick {
                    focusRequester.requestFocus()
                    true
                }
            },
        enabled = enabled,
        singleLine = true,
        textStyle = MiuixTheme.textStyles.main,
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        interactionSource = interactionSource,
        decorationBox =
        @Composable { innerTextField ->
            val shape = remember { derivedStateOf { SmoothRoundedCornerShape(50.dp) } }
            Box(
                modifier = Modifier
                    .background(
                        color = MiuixTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f),
                        shape = shape.value
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(paddingModifier),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = if (!(query.isNotEmpty() || expanded)) label else "",
                            color = MiuixTheme.colorScheme.onSurfaceContainerHigh
                        )

                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                }
            }
        }
    )

    val shouldClearFocus = !expanded && focused
    LaunchedEffect(expanded) {
        if (shouldClearFocus) {
            delay(100)
            focusManager.clearFocus()
        }
    }
}
