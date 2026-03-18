package com.suqi8.oshin.ui.mainscreen.about

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.YukiHookAPI_Impl
import com.suqi8.oshin.BuildConfig
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.CouiListItemPosition
import com.suqi8.oshin.ui.activity.components.FunArrow
import com.suqi8.oshin.ui.activity.components.SuperArrow
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.mainscreen.LocalColorMode
import com.suqi8.oshin.ui.theme.BgEffectView
import com.suqi8.oshin.utils.executeCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
import top.yukonga.miuix.kmp.utils.overScrollVertical
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.log10
import kotlin.math.pow

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UseKtx", "RestrictedApi",
    "UnrememberedMutableState"
)
@Composable
fun Main_About(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val showDeviceNameDialog = remember { mutableStateOf(false) }
    val deviceName: MutableState<String> = remember {
        mutableStateOf(
            Settings.Global.getString(
                context.contentResolver,
                "revise_device_name"
            ) ?: ""
        )
    }
    val deviceNameCache: MutableState<String> = remember { mutableStateOf(deviceName.value) }
    val physicalTotalStorage = formatSize(getPhysicalTotalStorage(context))
    val usedStorage = formatSize(getUsedStorage())

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        viewModel.exportSettings(uri)
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.importSettings(uri)
    }

    val bgAlpha = remember { mutableFloatStateOf(1f) }
    val mainAlpha = remember { mutableFloatStateOf(1f) }
    val mainScale = remember { mutableFloatStateOf(1f) }
    val secAlpha = remember { mutableFloatStateOf(1f) }
    val secScale = remember { mutableFloatStateOf(1f) }
    val scroll = rememberLazyListState()

    val density = LocalDensity.current

    LaunchedEffect(scroll) {
        val min = with(density) { 0.dp.toPx() }
        val sec = with(density) { 100.dp.toPx() }
        val main = with(density) { 160.dp.toPx() }
        val mainHeight = main - sec
        val bgHeight = with(density) { 332.dp.toPx() }

        snapshotFlow { Pair(scroll.firstVisibleItemIndex, scroll.firstVisibleItemScrollOffset) }
            .onEach { (index, offset) ->
                if (index == 0) {
                    val floatOffset = offset.toFloat()
                    val alpha = ((bgHeight - floatOffset / 1.6f).coerceIn(min, bgHeight) / bgHeight).coerceIn(0f, 1f)
                    bgAlpha.floatValue = alpha

                    val secValue = ((sec - floatOffset / 1.8f).coerceIn(min, sec) / sec).coerceIn(0f, 1f)
                    secAlpha.floatValue = secValue
                    secScale.floatValue = lerp(0.9f, 1f, secValue)
                    val mainValue = ((main - (floatOffset / 1.3f).coerceIn(sec, main)) / mainHeight).coerceIn(0f, 1f)
                    mainAlpha.floatValue = (mainValue * 1.5f)
                    mainScale.floatValue = lerp(0.9f, 1f, mainValue)
                } else {
                    bgAlpha.floatValue = 0f
                    secAlpha.floatValue = 0f
                    mainAlpha.floatValue = 0f
                }
            }.collect()
    }

    // --- 主题颜色修正 ---
    val colorModeState = LocalColorMode.current
    val systemIsDark = isSystemInDarkTheme()
    val isFinalDarkMode = when (colorModeState.value) {
        1 -> false // 1 = 白天
        2 -> true  // 2 = 黑夜
        else -> systemIsDark // 0 = 跟随系统
    }
    val bgEffectMode = if (isFinalDarkMode) 2 else 1 // BgEffectView 需要 1 或 2

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .offset(y = 50.dp),
            factory = { ctx -> BgEffectView(ctx, bgEffectMode) },
            update = {
                it.updateMode(bgEffectMode)
                it.alpha = bgAlpha.floatValue
            }
        )

        Column(
            modifier = Modifier
                .padding(top = 55.dp)
                .fillMaxWidth()
                .height(520.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val gradientColors = if (isFinalDarkMode) {
                listOf(Color("#D0A279ED".toColorInt()), Color("#D0E3BCB1".toColorInt()))
            } else {
                listOf(Color("#D03A18AD".toColorInt()), Color("#D0A56138".toColorInt()))
            }
            Text(
                text = "OShin ${BuildConfig.BUILD_TYPE_TAG}",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                style = TextStyle(brush = Brush.linearGradient(colors = gradientColors), alpha = mainAlpha.floatValue),
                modifier = Modifier.scale(mainScale.floatValue)
            )
            Text(
                text = context.packageManager.getPackageInfo(context.packageName, 0).versionName.toString(),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(secScale.floatValue)
                    .alpha(secAlpha.floatValue)
                    .padding(top = 20.dp),
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurfaceVariantSummary,
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            state = scroll,
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding()),
        ) {
            item {
                Spacer(modifier = Modifier.size(520.dp))
            }
            item {
                val cardAlpha by derivedStateOf {
                    if (scroll.firstVisibleItemIndex > 0) 1f else (scroll.firstVisibleItemScrollOffset.toFloat() / 1000f).coerceIn(0f, 1f)
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                        .alpha(cardAlpha)
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    FunArrow(
                        title = stringResource(R.string.Device_Name),
                        position = CouiListItemPosition.Top,
                        rightText = deviceName.value,
                        onClick = {
                            showDeviceNameDialog.value = true
                        }
                    )
                    if (showDeviceNameDialog.value) {
                        SuperDialog(
                            title = stringResource(R.string.Device_Name),
                            onDismissRequest = { showDeviceNameDialog.value = false },
                            show = showDeviceNameDialog
                        ) {
                            TextField(
                                value = deviceNameCache.value,
                                onValueChange = { deviceNameCache.value = it },
                                backgroundColor = colorScheme.secondaryContainer,
                                label = "",
                                modifier = Modifier.padding(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(R.string.cancel),
                                    onClick = { showDeviceNameDialog.value = false }
                                )
                                Spacer(Modifier.width(12.dp))
                                TextButton(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(R.string.ok),
                                    colors = ButtonDefaults.textButtonColorsPrimary(),
                                    onClick = {
                                        deviceName.value = deviceNameCache.value
                                        showDeviceNameDialog.value = false
                                        coroutineScope.launch(Dispatchers.IO) {
                                            executeCommand("settings put global revise_device_name '${deviceName.value}'")
                                        }
                                    }
                                )
                            }
                        }
                    }
                    addline()
                    FunArrow(title = stringResource(R.string.Device_Memory),
                        rightText = "$usedStorage / $physicalTotalStorage",
                        position = CouiListItemPosition.Bottom,
                        onClick = { openStorageSettings(context) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.size(12.dp)) }
            item {
                val cardAlpha by derivedStateOf {
                    if (scroll.firstVisibleItemIndex > 0) 1f else (scroll.firstVisibleItemScrollOffset.toFloat() / 1000f).coerceIn(0f, 1f)
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                        .alpha(cardAlpha)
                ) {
                    Text(deviceName.value + "", fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                    Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)) {
                        InfoItem(label = "Commit ID", value = context.packageManager.getPackageInfo(context.packageName, 0).versionName.toString().substringAfterLast("."))
                        InfoItem(label = stringResource(R.string.yuki_hook_api_version), value = YukiHookAPI.VERSION)
                        InfoItem(label = stringResource(R.string.compiled_timestamp), value = YukiHookAPI_Impl.compiledTimestamp.toString())
                        InfoItem(label = stringResource(R.string.compiled_time), value = timestampToDateTime(YukiHookAPI_Impl.compiledTimestamp))
                    }
                }
            }
            item { SmallTitle(text = stringResource(R.string.by_the_way)) }
            item { CommunityCard(context) }
            item { SmallTitle(text = stringResource(R.string.thank)) }
            item {
                ThanksCard(
                    navController = navController,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            item {
                SmallTitle(text = stringResource(R.string.config_management))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.export_config),
                            summary = stringResource(R.string.export_config_summary),
                            position = CouiListItemPosition.Top,
                            onClick = {
                                exportLauncher.launch("OShin_Config.json")
                            }
                        )
                        addline()
                        SuperArrow(
                            title = stringResource(R.string.import_config),
                            summary = stringResource(R.string.import_config_summary),
                            onClick = {
                                importLauncher.launch("application/json")
                            }
                        )
                        addline()
                        SuperArrow(
                            title = stringResource(R.string.clear_config),
                            summary = stringResource(R.string.clear_config_summary),
                            position = CouiListItemPosition.Bottom,
                            onClick = {
                                viewModel.clearAllSettings()
                            }
                        )
                    }
                }
            }
            item { SmallTitle(text = stringResource(R.string.other)) }
            item {
                AboutActionsCard(
                    navController = navController,
                    context = context,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    text = "Powered By SYCTeam & 酸奶",
                    fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onBackgroundVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = ""
        )
        val (shadowColor, backgroundColor, borderColor) = if (isFinalDarkMode) {
            Triple(Color(0x4D000000), Color(0x1FFFFFFF), integerArrayResource(R.array.my_card_stroke_gradient_colors_dark))
        } else {
            Triple(Color(0x40000000), Color(0x99FFFFFF), integerArrayResource(R.array.my_card_stroke_gradient_colors_light))
        }
        val buttonAlpha by derivedStateOf {
            if (scroll.firstVisibleItemIndex > 0) 0f else (1f - (scroll.firstVisibleItemScrollOffset.toFloat() / 300)).coerceIn(0f, 1f)
        }
        // button of check update
        with(sharedTransitionScope) {
            Button(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "update_card_transition"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
                    .padding(top = 430.dp)
                    .offset(y = -(scroll.firstVisibleItemScrollOffset.toFloat() / 3).dp)
                    .alpha(buttonAlpha)
                    .align(Alignment.TopCenter)
                    .scale(scale)
                    .drawBehind {
                        val strokeWidth = 1.5.dp.toPx()
                        val inset = strokeWidth / 2
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(borderColor[1]),
                                    Color(borderColor[0])
                                ),
                                start = Offset(size.width / 2, 0f),
                                end = Offset(size.width / 2, size.height)
                            ),
                            topLeft = Offset(inset, inset),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            style = Stroke(width = strokeWidth)
                        )
                    }
                    .shadow(
                        elevation = 1.5.dp,
                        shape = G2RoundedCornerShape(16.dp),
                        clip = true,
                        ambientColor = shadowColor,
                        spotColor = shadowColor
                    ),
                onClick = { navController.navigate("software_update") },
                interactionSource = interactionSource,
                colors = backgroundColor
            ) {
                Text(text = stringResource(R.string.check_update), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
            }
        }
    }
}

// --- 以下为未改动的辅助组件和函数 ---

@Composable
private fun InfoItem(label: String, value: String) {
    Column(Modifier.padding(bottom = 10.dp)) {
        Text(value, fontSize = 14.sp)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun CommunityCard(context: Context) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .padding(bottom = 6.dp)) {
        Card(Modifier.padding(10.dp)) {
            Image(painter = painterResource(R.drawable.qq_pic_merged_1727926207595), contentDescription = null, modifier = Modifier.fillMaxWidth())
        }
        val toastMessage = stringResource(R.string.please_install_cool_apk)
        FunArrow(title = stringResource(R.string.go_to_his_homepage),
            position = CouiListItemPosition.Bottom,
            onClick = {
            val coolApkUri = "coolmarket://u/894238".toUri()
            val intent = Intent(Intent.ACTION_VIEW, coolApkUri)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ThanksCard(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .padding(bottom = 6.dp)) {
        item(
            name = "酸奶",
            coolapk = "Stracha酸奶菌",
            coolapkid = 15225420,
            github = "suqi8",
            qq = 3383787570
        )
        addline()
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "about_contributors"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                FunArrow(title = stringResource(R.string.contributors), onClick = { navController.navigate("about_contributors") })
            }
        }
        addline()
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "about_references"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                FunArrow(
                    title = stringResource(R.string.references),
                    position = CouiListItemPosition.Bottom,
                    onClick = { navController.navigate("about_references") }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AboutActionsCard(
    navController: NavController,
    context: Context,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 6.dp)
    ) {
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "about_setting"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                IconFunArrow(
                    title = stringResource(R.string.settings),
                    iconRes = R.drawable.settings,
                    position = CouiListItemPosition.Top,
                    onClick = { navController.navigate("about_setting") })
            }
        }
        addline()
        IconFunArrow(
            title = stringResource(R.string.donors),
            iconRes = R.drawable.donors,
            onClick = { openUrl(context, "https://oshin.mikusignal.top/docs/donate.html") })
        addline()
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "about_group"), // <-- Key
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                IconFunArrow(
                    title = stringResource(R.string.official_channel),
                    iconRes = R.drawable.group,
                    onClick = { navController.navigate("about_group") })
            }
        }
        addline()
        IconFunArrow(
            title = stringResource(R.string.official_website),
            iconRes = R.drawable.website,
            onClick = { openUrl(context, "https://oshin.mikusignal.top/") })
        addline()
        IconFunArrow(
            title = "GitHub",
            summary = stringResource(R.string.github_summary),
            iconRes = R.drawable.github,
            onClick = { openUrl(context, "https://github.com/suqi8/OShin") })
        addline()
        IconFunArrow(
            title = stringResource(R.string.contribute_translation),
            summary = stringResource(R.string.crowdin_contribute_summary),
            iconRes = R.drawable.translators,
            position = CouiListItemPosition.Bottom,
            onClick = {
                openUrl(
                    context,
                    "https://github.com/suqi8/OShin/tree/master/app/src/main/res"
                )
            })
    }
}

@Composable
fun IconFunArrow(
    title: String,
    summary: String? = null,
    iconRes: Int,
    position: CouiListItemPosition = CouiListItemPosition.Single,
    onClick: () -> Unit
) {
    FunArrow(
        title = title,
        summary = summary,
        position = position,
        leftAction = {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(colorScheme.onSurface)
            )
        },
        onClick = onClick
    )
}

@Composable
fun Button(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, cornerRadius: Dp = ButtonDefaults.CornerRadius, minWidth: Dp = ButtonDefaults.MinWidth, minHeight: Dp = ButtonDefaults.MinHeight, colors: Color = colorScheme.secondaryVariant, insideMargin: PaddingValues = ButtonDefaults.InsideMargin, interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }, content: @Composable RowScope.() -> Unit) {
    Surface(modifier = modifier
        .semantics { role = Role.Button }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        ), shape = G2RoundedCornerShape(cornerRadius), color = colors) {
        Row(Modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .padding(insideMargin), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, content = content)
    }
}

@SuppressLint("UseKtx")
fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

private fun openStorageSettings(context: Context) {
    try {
        val intent = Intent().setClassName("com.android.settings",
            $$"com.android.settings.Settings$StorageDashboardActivity"
        )
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "无法打开存储管理页面", Toast.LENGTH_SHORT).show()
    }
}

fun timestampToDateTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}

fun getPhysicalTotalStorage(context: Context): Long {
    val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

    val primaryVolume = storageManager.storageVolumes.firstOrNull { it.isPrimary }

    val path = primaryVolume?.directory?.path

    if (path != null) {
        try {
            val statFs = StatFs(path)
            return statFs.blockCountLong * statFs.blockSizeLong
        } catch (e: IllegalArgumentException) {
            // StatFs 可能会因为路径无效而抛出异常
            e.printStackTrace()
        }
    }

    // 如果以上方法失败，则使用备用方法
    return getTotalStorage()
}

@SuppressLint("DefaultLocale")
fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return String.format("%.2f %s", size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}

fun getTotalStorage(): Long {
    return try { StatFs(Environment.getDataDirectory().path).totalBytes }
    catch (e: Exception) { 0L }
}

fun getAvailableStorage(): Long {
    return try { StatFs(Environment.getDataDirectory().path).availableBytes }
    catch (e: Exception) { 0L }
}

fun getUsedStorage(): Long {
    val totalStorage = getTotalStorage()
    val availableStorage = getAvailableStorage()
    return totalStorage - availableStorage
}
