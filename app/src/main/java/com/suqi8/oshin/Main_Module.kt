package com.suqi8.oshin

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.utils.GetAppIconAndName
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.text.Collator
import java.util.Locale

// --- 数据模型 ---
data class AppInfo(val packageName: String, val activityName: String)
data class FeatureItem(val title: String, val summary: String? = null, val category: String)
var notInstalledApps = mutableStateOf<Set<String>>(emptySet())

// --- UI 入口 (Composable) ---
@Composable
fun Main_Module(
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    padding: PaddingValues
) {
    val context = LocalContext.current

    // --- 状态管理 ---
    var searchValue by remember { mutableStateOf("") }
    var appStyle by remember { mutableStateOf(context.prefs("settings").getInt("appstyle", 0)) }
    // 修复：明确泛型类型，并正确处理类型映射
    val collator = remember { Collator.getInstance(Locale.CHINA) }
    val filteredFeatures by remember(searchValue) {
        derivedStateOf {
            if (searchValue.isBlank()) {
                emptyList()
            } else {
                features(context)
                    .map { originalItem: FeatureItem ->
                        FeatureItem(originalItem.title, originalItem.summary, originalItem.category)
                    }
                    .filter {
                        it.title.contains(searchValue, ignoreCase = true) ||
                                it.summary?.contains(searchValue, ignoreCase = true) ?: false
                    }
                    .sortedWith(compareBy(collator) { it.title })
            }
        }
    }

    // --- 主布局 ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background)
    ) {
        // 应用HUD风格的动态背景
        HUDBackground()

        // 使用LazyColumn构建主滚动列表
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 搜索栏
            item {
                HUDSearchBar(
                    query = searchValue,
                    onQueryChange = { searchValue = it },
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )
            }

            // 搜索结果或应用列表
            item {
                // animateContentSize 使内容切换时有平滑的动画效果
                Column(modifier = Modifier.animateContentSize()) {
                    // 如果有搜索词，显示搜索结果
                    if (searchValue.isNotBlank()) {
                        SearchContent(features = filteredFeatures, query = searchValue, navController = navController)
                    } else {
                        // 否则，显示应用列表
                        AppListContent(
                            appStyle = appStyle,
                            onStyleChange = { appStyle = it },
                            notInstalledApps = notInstalledApps.value,
                            onAppNotFound = { packageName ->
                                notInstalledApps.value = notInstalledApps.value + packageName
                            },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

// --- UI 组件 ---

/**
 * HUD风格的搜索栏
 */
@Composable
fun HUDSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .clip(CutCornerShape(8.dp))
            .background(MiuixTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            .border(1.dp, MiuixTheme.colorScheme.primary.copy(alpha = 0.3f), CutCornerShape(8.dp))
            .padding(horizontal = 8.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            textStyle = MiuixTheme.textStyles.main.copy(color = MiuixTheme.colorScheme.onBackground, fontFamily = FontFamily.Monospace),
            singleLine = true,
            cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Search,
                        contentDescription = "Search",
                        tint = MiuixTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.Search),
                                color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

/**
 * 搜索结果内容
 */
@Composable
fun SearchContent(features: List<FeatureItem>, query: String, navController: NavController) {
    val highlightColor = MiuixTheme.colorScheme.primary

    HUDModuleContainer(modifier = Modifier.padding(horizontal = 16.dp)) {
        if (features.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text(text = "空空如也~", fontFamily = FontFamily.Monospace, color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            }
        } else {
            Column {
                features.forEachIndexed { index, feature ->
                    SearchListItem(
                        feature = feature,
                        query = query,
                        highlightColor = highlightColor,
                        onClick = { navController.navigate(feature.category) }
                    )
                    if (index < features.size - 1) {
                        addline()
                    }
                }
            }
        }
    }
}

@Composable
fun SearchListItem(
    feature: FeatureItem,
    query: String,
    highlightColor: Color,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val route = remember(feature.category) { GetFuncRoute(feature.category, context) }
    val summary = if (feature.summary != null) "${feature.summary}\n$route" else route

    val titleAnnotated = highlightMatches(feature.title, query, highlightColor)
    val summaryAnnotated = highlightMatches(summary, query, highlightColor)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = titleAnnotated, fontSize = 16.sp, color = MiuixTheme.colorScheme.onBackground)
            Text(text = summaryAnnotated, fontSize = 12.sp, color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
    }
}


/**
 * 应用列表内容
 */
@Composable
fun AppListContent(
    appStyle: Int,
    onStyleChange: (Int) -> Unit,
    notInstalledApps: Set<String>,
    onAppNotFound: (String) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val appList = remember {
        listOf(
            AppInfo("android", "android"),
            AppInfo("com.android.systemui", "systemui"),
            AppInfo("com.android.settings", "settings"),
            AppInfo("com.android.launcher", "launcher"),
            AppInfo("com.oplus.battery", "battery"),
            AppInfo("com.heytap.speechassist", "speechassist"),
            AppInfo("com.coloros.ocrscanner", "ocrscanner"),
            AppInfo("com.oplus.games", "games"),
            AppInfo("com.finshell.wallet", "wallet"),
            AppInfo("com.coloros.phonemanager", "phonemanager"),
            AppInfo("com.oplus.phonemanager", "oplusphonemanager"),
            AppInfo("com.android.mms", "mms"),
            AppInfo("com.coloros.securepay", "securepay"),
            AppInfo("com.heytap.health", "health"),
            AppInfo("com.oplus.appdetail", "appdetail"),
            AppInfo("com.heytap.quicksearchbox", "quicksearchbox"),
            AppInfo("com.mi.health", "mihealth"),
            AppInfo("com.oplus.ota", "ota"),
            AppInfo("com.coloros.oshare", "oshare"),
            AppInfo("com.android.incallui", "incallui"),
            AppInfo("com.oplus.notificationmanager", "notificationmanager"),
            AppInfo("com.oplus.exsystemservice", "exsystemservice"),
            AppInfo("com.android.phone", "phone")
        )
    }

    val installedApps = remember(appList, notInstalledApps) {
        appList.filter { it.packageName !in notInstalledApps }
    }

    Column(Modifier.padding(horizontal = 16.dp)) {
        SectionTitle(titleResId = R.string.section_title_apps)
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.switch_style),
                color = MiuixTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    val newStyle = if (appStyle == 0) 1 else 0
                    onStyleChange(newStyle)
                    context.prefs("settings").edit { putInt("appstyle", newStyle) }
                }
            )
        }

        HUDModuleContainer {
            if (appStyle == 0) {
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    installedApps.forEach { appInfo ->
                        FunctionAppFlow(
                            packageName = appInfo.packageName,
                            activityName = appInfo.activityName,
                            navController = navController,
                            onResult = onAppNotFound
                        )
                    }
                }
            } else {
                Column {
                    installedApps.forEachIndexed { index, appInfo ->
                        FunctionApp(
                            packageName = appInfo.packageName,
                            activityName = appInfo.activityName,
                            navController = navController,
                            onResult = onAppNotFound
                        )
                        if (index < installedApps.size - 1) {
                            addline()
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        SuperArrow(
            title = stringResource(R.string.app_not_found_in_list),
            titleColor = BasicComponentDefaults.titleColor(color = MiuixTheme.colorScheme.primary),
            onClick = { navController.navigate("hide_apps_notice") }
        )
    }
}

// --- 恢复原始的应用列表项组件 ---

internal val colorCache = mutableMapOf<String, Color>()

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FunctionApp(packageName: String, activityName: String, navController: NavController, onResult: (String) -> Unit) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != "noapp") {
            val defaultColor = MiuixTheme.colorScheme.surface
            val noModuleActive = Color.Red

            val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
            val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

            LaunchedEffect(icon, dominantColor.value) {
                if (isLoading.value) {
                    val newColor = withContext(Dispatchers.IO) {
                        if (YukiHookAPI.Status.isModuleActive) getAutoColor(icon) else noModuleActive
                    }
                    dominantColor.value = newColor
                    colorCache[packageName] = newColor
                    isLoading.value = false
                }
            }

            Row(
                modifier = Modifier
                    .clickable { navController.navigate(activityName) }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.defaultColors(color = dominantColor.value),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .drawColoredShadow(
                            dominantColor.value,
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
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = appName)
                    Text(
                        text = packageName,
                        fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = MiuixTheme.colorScheme.onBackgroundVariant
                    )
                }
            }
        } else {
            onResult(packageName)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FunctionAppFlow(packageName: String, activityName: String, navController: NavController, onResult: (String) -> Unit) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != "noapp") {
            val defaultColor = MiuixTheme.colorScheme.surface
            val noModuleActive = Color.Red

            val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
            val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

            LaunchedEffect(icon, dominantColor.value) {
                if (isLoading.value) {
                    val newColor = withContext(Dispatchers.IO) {
                        if (YukiHookAPI.Status.isModuleActive) getAutoColor(icon) else noModuleActive
                    }
                    dominantColor.value = newColor
                    colorCache[packageName] = newColor
                    isLoading.value = false
                }
            }

            Column(
                modifier = Modifier
                    .clickable { navController.navigate(activityName) }
                    .width(75.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.defaultColors(color = dominantColor.value),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .drawColoredShadow(
                            dominantColor.value,
                            1f,
                            borderRadius = 13.dp,
                            shadowRadius = 7.dp,
                            offsetX = 0.dp,
                            offsetY = 0.dp,
                            roundedRect = false
                        )
                ) {
                    Image(bitmap = icon, contentDescription = "App Icon", modifier = Modifier.size(50.dp))
                }
                Text(
                    text = appName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                )
            }
        } else {
            onResult(packageName)
        }
    }
}


// --- 辅助函数和缓存 ---

@Composable
fun highlightMatches(text: String, query: String, highlightColor: Color): AnnotatedString {
    return buildAnnotatedString {
        if (query.isBlank() || !text.contains(query, ignoreCase = true)) {
            append(text)
            return@buildAnnotatedString
        }
        val regex = Regex(query, RegexOption.IGNORE_CASE)
        var lastIndex = 0
        regex.findAll(text).forEach { matchResult ->
            append(text.substring(lastIndex, matchResult.range.first))
            withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                append(matchResult.value)
            }
            lastIndex = matchResult.range.last + 1
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}

@Composable
fun HUDModuleContainer(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = CutCornerShape(8.dp)
    Column(
        modifier = modifier.clip(shape).background(MiuixTheme.colorScheme.onBackground.copy(alpha = 0.03f))
            .border(1.dp, MiuixTheme.colorScheme.primary.copy(alpha = 0.3f), shape).padding(8.dp)
    ) { content() }
}

@SuppressLint("UseKtx")
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    roundedRect: Boolean = true
) = this.drawBehind {
    val transparentColor = color.copy(alpha = .0f).value.toLong().toColorInt()
    val shadowColor = color.copy(alpha = alpha).value.toLong().toColorInt()
    this.drawIntoCanvas {
        val paint = Paint()
        paint.color = Color.Transparent
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            if (roundedRect) this.size.height / 2 else borderRadius.toPx(),
            if (roundedRect) this.size.height / 2 else borderRadius.toPx(),
            paint
        )
    }
}

suspend fun getAutoColor(icon: ImageBitmap): Color {
    return withContext(Dispatchers.IO) {
        Palette.from(icon.asAndroidBitmap()).generate().dominantSwatch?.rgb?.let { Color(it) } ?: Color.White
    }
}

fun features(context: Context) = listOf(
    FeatureItem(title = context.getString(R.string.downgr),
        summary = context.getString(R.string.downgr_summary),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.authcreak),
        summary = context.getString(R.string.authcreak_summary),
        category = "android\\package_manager_services"),
    FeatureItem(
        title = context.getString(R.string.digestCreak),
        summary = context.getString(R.string.digestCreak_summary),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.UsePreSig),
        summary = context.getString(R.string.UsePreSig_summary),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.enhancedMode),
        summary = context.getString(R.string.enhancedMode_summary),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.bypassBlock),
        summary = context.getString(R.string.bypassBlock_summary),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.shared_user_title),
        summary = context.getString(R.string.shared_user_summary),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.disable_verification_agent_title),
        summary = context.getString(R.string.disable_verification_agent_summary),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.package_manager_services),
        category = "android\\package_manager_services"),
    FeatureItem(title = context.getString(R.string.oplus_system_services),
        category = "android\\oplus_system_services"),
    FeatureItem(title = context.getString(R.string.oplus_root_check),
        summary = context.getString(R.string.oplus_root_check_summary),
        category = "android\\oplus_system_services"),
    FeatureItem(title = context.getString(R.string.desktop_icon_and_text_size_multiplier),
        summary = context.getString(R.string.icon_size_limit_note),
        category = "launcher"),
    FeatureItem(title = context.getString(R.string.power_consumption_indicator),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.dual_cell),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.absolute_value),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.bold_text),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.alignment),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.update_time),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.font_size),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.dual_row_title),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.first_line_content),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.second_line_content),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.power),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.current),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.voltage),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.temperature_indicator),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.show_cpu_temp_data),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.change_cpu_temp_source),
        summary = context.getString(R.string.enter_thermal_zone_number),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.bold_text),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.alignment),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.update_time),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.font_size),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.dual_row_title),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.first_line_content),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.second_line_content),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.battery_temperature),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.cpu_temperature),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.status_bar_clock),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.hardware_indicator),
        category = "systemui\\hardware_indicator"),
    FeatureItem(title = context.getString(R.string.status_bar_icon),
        category = "systemui\\statusbar_icon"),
    FeatureItem(title = context.getString(R.string.hide_status_bar),
        category = "systemui"),
    FeatureItem(title = context.getString(R.string.enable_all_day_screen_off),
        category = "systemui"),
    FeatureItem(title = context.getString(R.string.force_trigger_ltpo),
        category = "systemui"),
    FeatureItem(title = context.getString(R.string.status_bar_clock),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_style),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_size),
        summary = context.getString(R.string.clock_size_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_update_time_title),
        summary = context.getString(R.string.clock_update_time_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = "dp To px",
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_top_margin),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_bottom_margin),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_left_margin),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_right_margin),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.show_years_title),
        summary = context.getString(R.string.show_years_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.show_month_title),
        summary = context.getString(R.string.show_month_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.show_day_title),
        summary = context.getString(R.string.show_day_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.show_week_title),
        summary = context.getString(R.string.show_week_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.show_cn_hour_title),
        summary = context.getString(R.string.show_cn_hour_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.showtime_period_title),
        summary = context.getString(R.string.showtime_period_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.show_seconds_title),
        summary = context.getString(R.string.show_seconds_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.show_millisecond_title),
        summary = context.getString(R.string.show_millisecond_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.hide_space_title),
        summary = context.getString(R.string.hide_space_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.dual_row_title),
        summary = context.getString(R.string.dual_row_summary),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.alignment),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_format),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.clock_format_example),
        category = "systemui\\status_bar_clock"),
    FeatureItem(title = context.getString(R.string.status_bar_icon),
        category = "systemui\\statusbar_icon"),
    FeatureItem(title = context.getString(R.string.wifi_icon),
        category = "systemui\\statusbar_icon"),
    FeatureItem(title = context.getString(R.string.wifi_arrow),
        category = "systemui\\statusbar_icon"),
    FeatureItem(title = context.getString(R.string.force_display_memory),
        category = "launcher\\recent_task"),
    FeatureItem(title = context.getString(R.string.recent_tasks),
        category = "launcher\\recent_task"),
    FeatureItem(title = context.getString(R.string.status_bar_notification),
        category = "systemui\\notification"),
    FeatureItem(title = context.getString(R.string.remove_developer_options_notification),
        summary = context.getString(R.string.notification_restriction_message),
        category = "systemui\\notification"),
    FeatureItem(title = context.getString(R.string.low_battery_fluid_cloud_off),
        category = "battery"),
    FeatureItem(title = context.getString(R.string.remove_and_do_not_disturb_notification),
        summary = context.getString(R.string.notification_restriction_message),
        category = "systemui\\notification"),
    FeatureItem(title = context.getString(R.string.force_enable_xiaobu_call),
        category = "speechassist"),
    FeatureItem(title = context.getString(R.string.remove_full_screen_translation_restriction),
        category = "ocrscanner"),
    FeatureItem(title = context.getString(R.string.enable_ultra_combo),
        category = "games"),
    FeatureItem(title = context.getString(R.string.enable_hok_ai_v1),
        category = "games"),
    FeatureItem(title = context.getString(R.string.enable_hok_ai_v2),
        summary = context.getString(R.string.realme_gt7pro_feature_unlock_device_restriction),
        category = "games"),
    FeatureItem(title = context.getString(R.string.enable_hok_ai_v3),
        category = "games"),
    FeatureItem(title = context.getString(R.string.feature_disable_cloud_control),
        category = "games"),
    FeatureItem(title = context.getString(R.string.remove_package_restriction),
        category = "games"),
    FeatureItem(title = context.getString(R.string.enable_all_features),
        summary = context.getString(R.string.enable_all_features_warning),
        category = "games"),
    FeatureItem(title = context.getString(R.string.enable_pubg_ai),
        category = "games"),
    FeatureItem(title = context.getString(R.string.auto_start_max_limit),
        summary = context.getString(R.string.auto_start_default_hint),
        category = "battery"),
    FeatureItem(title = context.getString(R.string.split_screen_multi_window),
        category = "android\\split_screen_multi_window"),
    FeatureItem(title = context.getString(R.string.remove_all_small_window_restrictions),
        category = "android\\split_screen_multi_window",
    ),
    FeatureItem(title = context.getString(R.string.force_multi_window_mode),
        category = "android\\split_screen_multi_window"),
    FeatureItem(title = context.getString(R.string.max_simultaneous_small_windows),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    FeatureItem(title = context.getString(R.string.small_window_corner_radius),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    FeatureItem(title = context.getString(R.string.small_window_focused_shadow),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    FeatureItem(title = context.getString(R.string.small_window_unfocused_shadow),
        category = "android\\split_screen_multi_window",
        summary = context.getString(R.string.default_value_hint_negative_one)),
    FeatureItem(title = context.getString(R.string.custom_display_model),
        summary = context.getString(R.string.hint_empty_content_default),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.remove_swipe_page_ads),
        summary = context.getString(R.string.clear_wallet_data_notice),
        category = "wallet"),
    FeatureItem(title = context.getString(R.string.enable_ota_card_bg),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.select_background_btn),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.corner_radius_title),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.force_enable_fold_mode),
        category = "launcher"),
    FeatureItem(title = context.getString(R.string.fold_mode),
        category = "launcher"),
    FeatureItem(title = context.getString(R.string.force_enable_fold_dock),
        category = "launcher"),
    FeatureItem(title = context.getString(R.string.adjust_dock_transparency),
        category = "launcher"),
    FeatureItem(title = context.getString(R.string.force_enable_dock_blur),
        summary = context.getString(R.string.force_enable_dock_blur_undevice),
        category = "launcher"),
    FeatureItem(title = context.getString(R.string.remove_game_filter_root_detection),
        category = "games"),
    FeatureItem(title = context.getString(R.string.remove_all_popup_delays),
        summary = context.getString(R.string.remove_all_popup_delays_eg),
        category = "phonemanager"),
    FeatureItem(title = context.getString(R.string.remove_all_popup_delays),
        summary = context.getString(R.string.remove_all_popup_delays_eg),
        category = "oplusphonemanager"),
    FeatureItem(title = context.getString(R.string.remove_message_ads),
        category = "mms"),
    FeatureItem(title = context.getString(R.string.force_show_nfc_security_chip),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.security_payment_remove_risky_fluid_cloud),
        category = "securepay"),
    FeatureItem(title = context.getString(R.string.custom_score),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "phonemanager"),
    FeatureItem(title = context.getString(R.string.custom_prompt_content),
        category = "phonemanager"),
    FeatureItem(title = context.getString(R.string.custom_animation_duration),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "phonemanager"),
    FeatureItem(title = context.getString(R.string.custom_score),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "oplusphonemanager"),
    FeatureItem(title = context.getString(R.string.custom_prompt_content),
        category = "oplusphonemanager"),
    FeatureItem(title = context.getString(R.string.custom_animation_duration),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "oplusphonemanager"),
    FeatureItem(title = context.getString(R.string.feature),
        category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_enable_all_features),summary = context.getString(R.string.enable_all_features_warning),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.demo_only_device),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.retail_locked_terminal),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_enable_karaoke),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_enable_3d_camera_color),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_aon_explorer),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_enable_app_freeze),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.check_ble_audio_whitelist),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_breathing_light_sync),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_breathing_light_color),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_support_wide_gamut),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_support_color_mode),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_support_hidden_app_feature),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_support_smart_case),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_dirac_audio),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_dolby_audio),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_dual_earbuds),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_foldable_screen),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_fold_or_flip_screen),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_display_remapping),summary = context.getString(R.string.disable_ui_remap_when_unfolded),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_gesture_navigation),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_google_mobile_services),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_storage_info),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_holo_audio),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_hd_video),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.auto_grant_install),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_lock_wallpaper),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.light_os),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_multi_volume),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_app_clone),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_adaptive_brightness),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_ota),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_audio_boost),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_ai_image),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_osie_tech),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_shutdown_key),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.single_pulse_pwm),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_res_switch),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.manual_refresh_rate),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.default_smart_refresh),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.refresh_rate_notify),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_sell_mode),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_dual_sim),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_single_sim_check),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_anti_voyeur),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_snc_content),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_sound_combo),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_sound_settings),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_audio_input),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_15k_resolution),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_adfr),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_aod),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_aon_face),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_autolayout),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_blade_colormode),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_breeno_suggest),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_brightness_anim),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_cinema_mode),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_oled_colorful),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_custom_color),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_colorful_mode),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_powersaving_color),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_compact_window),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_dc_backlight),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_dynamic_brightness),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_dirac_a2dp),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_dynamic_fps),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_edge_anti_touch),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_5g_support),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_fold_remap),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_gt_mode),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_hdr_alwayson),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_hdr_highlight),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_smart_color_temp2),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_linear_vibration),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_luxun_vibration),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_multi_led_breathing),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_phone_limit),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_pixelworks_x7),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_resolution_switch),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_ringtone_vibration),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_satellite_network),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_spatializer_speaker),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_super_volume2x),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_super_volume3x),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_temp_adjust),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_touchpad_split),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_ultrasonic_fp),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_volume_boost),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_color_ball),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_surround_effect),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_tablet_mode),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_typec_menu),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_ultrasonic_security),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_vibrator_style),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_smart_screenoff),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_richtap_vibrate),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_dirac_v2),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_iris5_display),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_ring_haptic),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_video_osie),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_video_sr),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_deactivate_app),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_haptic_preview),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_modify_devname),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_super_sleep),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_5g_reminder),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_account_dialog),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_app_disable),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_cmiit_auth),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_hyper_vision),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_carrier),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.locale_uk_to_en),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_clear_cache),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_colorful_real),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_confidential),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_cyberpunk),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.auto_resolution),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_oem_unlock),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_auto_rotate),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_app_switch),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_euex),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_exp_version),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_film_finger),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_finger_anim),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_fintech_nfc),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_flip_device),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_gesture),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.keep_gesture_up),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.more_gesture_up),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_gota_update),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_business_state),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_ultimate_clean),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_hw_version),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_device_id),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_ktv_loopback),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_mms_ringtone),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.move_dc_to_dev),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_network_speed),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_power_wake3),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_sim_signal),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_humming),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_kernel_id),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.ignore_repeat_click),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.imei_sv_from_ota),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_light_func),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_marvel),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_portrait_center),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_video_beauty),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_2g3g),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_ocloud),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_oh_device),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.only_hw_version),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_kddi_au),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_operator),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_privacy_email),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.keep_swipe_up),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_ota),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_otg_alarm),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_otg_entry),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_pac_custom),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_privacy),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_fake_base),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_rl_delete),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_rlm_device),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_raise_wake),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_recent_task),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.remove_cota_home),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_resize_screen),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_rlm_feedback),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_screen_pin),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_search_index),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_seedling_exp),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_custom_devname),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_cota_devname),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_set_password),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_all_anr),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_brand_name),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_carrier_config),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_carrier_update),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_custom_details),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_data_usage),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_diagnostic),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_os_firstname),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_hw_version),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_ims_status),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_kernel_time),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_net_unlock),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_never_timeout),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_npu_detail),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_processor),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_processor_gen2),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.screen_size_cm),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_sw_version),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.sw_instead_build),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_uicc_unlock),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_sim_lock),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_sim_toolkit),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.force_software_conf),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.special_side_finger),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_circle_search),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_custom_ver),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_electronic_label),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.fullscreen_apps),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.smart_gesture),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_imsi),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_meid),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.member_rcc_show),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.mini_capsule),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.number_recognition),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_oguard),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.oh_india_version),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.usb_tether_boot),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.quick_app_support),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.region_picker),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_roulette),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_wfc_dialog),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.smart_touch),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.smart_touch_v2),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.show_sms_number),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.ai_eye_protect),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_edge_panel),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_stable_plan),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_time_change),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_gaze_ringtone),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_user_exp),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_verify_dialog),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.virtual_comm_device),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.virtual_comm_service),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_vowifi_setting),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_volte_setting),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.volte_icon_off),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_wifi_setting),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hide_install_sources),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.biometric_privacy),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.upload_error_log),category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.dirac_sound), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.dolby_support), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.edge_panel), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.resolution_pic), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.sharpness_switch), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.hyper_mode), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.fluid_cloud), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.linear_vibration), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.op7_vibration), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.palm_unlock), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.stealth_security), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.pwm_reboot), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.anti_voyeur), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.enable_redpacket_helper), category = "settings\\feature"),
    FeatureItem(title = context.getString(R.string.disable_root_dialog),
        category = "health"),
    FeatureItem(title = context.getString(R.string.remove_recommendations),
        category = "appdetail"),
    FeatureItem(title = context.getString(R.string.network_speed_indicator),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.network_speed_indicator),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.network_speed_style),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.speed_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.unit_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.upload_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.download_font_size),
        summary = context.getString(R.string.default_value_hint_negative_one),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.slow_speed_threshold),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.hide_on_slow),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.hide_when_both_slow),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.icon_indicator),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.position_speed_indicator_front),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.hide_space),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.hide_bs),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.swap_upload_download),
        category = "systemui\\status_bar_wifi"),
    FeatureItem(title = context.getString(R.string.disable_72h_verify),
        category = "android"),
    FeatureItem(title = context.getString(R.string.allow_untrusted_touch),
        category = "android"),
    FeatureItem(title = context.getString(R.string.remove_app_recommendation_ads),
        category = "quicksearchbox"),
    FeatureItem(title = context.getString(R.string.accessibility_service_authorize),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.accessibility_service_direct),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.smart_accessibility_service),
        summary = context.getString(R.string.whitelist_app_auto_authorization),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.accessibility_whitelist),
        category = "settings"),
    FeatureItem(title = context.getString(R.string.remove_installation_frequency_popup),
        category = "appdetail"),
    FeatureItem(title = context.getString(R.string.remove_attempt_installation_popup),
        category = "appdetail"),
    FeatureItem(title = context.getString(R.string.remove_version_check),
        category = "appdetail"),
    FeatureItem(title = context.getString(R.string.remove_security_check),
        category = "appdetail"),
    FeatureItem(title = context.getString(R.string.enable_alarm_reminder),
        summary = context.getString(R.string.alarm_reminder_description),
        category = "mihealth"),
    FeatureItem(title = context.getString(R.string.remove_system_update_dialog),
        category = "ota"),
    FeatureItem(title = context.getString(R.string.remove_system_update_notification),
        category = "ota"),
    FeatureItem(title = context.getString(R.string.remove_wlan_auto_download_dialog),
        category = "ota"),
    FeatureItem(title = context.getString(R.string.remove_unlock_and_dmverity_check),
        category = "ota"),
    FeatureItem(title = context.getString(R.string.enable_mlbb_ai_god_assist),
        category = "games"),
    FeatureItem(title = context.getString(R.string.remove_oshare_auto_off),
        category = "oshare"),
    FeatureItem(title = context.getString(R.string.set_anim_level),
        category = "launcher"),
    FeatureItem(title = context.getString(R.string.hide_call_ringtone),
        category = "incallui"),
    FeatureItem(title = context.getString(R.string.enlarge_media_cover),
        summary = context.getString(R.string.media_cover_background_description),
        category = "systemui\\controlCenter"),
    FeatureItem(title = context.getString(R.string.remove_active_vpn_notification),
        summary = context.getString(R.string.reboot_required_to_take_effect),
        category = "systemui\\notification"),
    FeatureItem(title = context.getString(R.string.remove_charging_complete_notification),
        category = "systemui\\notification"),
    FeatureItem(title = context.getString(R.string.qs_media_auto_color_label),
        category = "systemui\\controlCenter"),
    FeatureItem(title = context.getString(R.string.allow_turn_off_all_categories),
        summary = context.getString(R.string.enable_all_category_control_summary),
        category = "notificationmanager"),
    FeatureItem(title = context.getString(R.string.disable_data_transfer_auth),
        category = "systemui"),
    FeatureItem(title = context.getString(R.string.usb_default_file_transfer),
        category = "systemui"),
    FeatureItem(title = context.getString(R.string.remove_usb_selection_dialog),
        category = "systemui"),
    FeatureItem(title = context.getString(R.string.toast_force_show_app_icon),
        summary = context.getString(R.string.toast_icon_source_module),
        category = "systemui"),
    FeatureItem(title = context.getString(R.string.remove_system_tamper_warning),
        category = "exsystemservice"),
    FeatureItem(title = context.getString(R.string.sms_verification_code),
        category = "phone"),
    FeatureItem(title = context.getString(R.string.sms_code_keyword),
        category = "phone")
)
