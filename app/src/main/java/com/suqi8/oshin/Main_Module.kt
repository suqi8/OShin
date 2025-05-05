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
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.ui.activity.funlistui.SearchList
import com.suqi8.oshin.ui.activity.funlistui.addline
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.overScrollVertical
import java.text.Collator
import java.util.Locale

fun features(context: Context) = listOf(
    item(title = context.getString(R.string.downgr),
        summary = context.getString(R.string.downgr_summary),
        category = "android\\package_manager_services")
)

var notInstallList = mutableStateOf(emptyList<String>())

@SuppressLint("UnrememberedMutableState")
@Composable
fun Main_Module(
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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        if (!expanded) {
            val appList = listOf(
                AppInfo("com.oplus.appdetail", "appdetail"),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .overScrollVertical()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                item {
                    Spacer(modifier = Modifier.size(68.dp + padding.calculateTopPadding()))
                }
                item {
                    val appstyle = rememberSaveable { mutableStateOf(context.prefs("settings").getInt("appstyle", 0)) }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                            .clickable {
                                appstyle.value = if (appstyle.value == 0) 1 else 0
                                context.prefs("settings").edit { putInt("appstyle", appstyle.value) }
                            },
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(text = stringResource(R.string.switch_style), color = MiuixTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        AnimatedVisibility(appstyle.value == 0) {
                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                appList.forEachIndexed { index, appInfo ->
                                    val notInstall = rememberSaveable { mutableStateOf(false) }
                                    FunctionAppFlow(
                                        packageName = appInfo.packageName,
                                        activityName = appInfo.activityName,
                                        navController = navController
                                    ) { result ->
                                        if (result == "noapp") {
                                            if (!notInstallList.value.contains(appInfo.packageName)) {
                                                notInstallList.value += appInfo.packageName
                                            }
                                            notInstall.value = true
                                        }
                                    }
                                }
                            }
                        }
                        AnimatedVisibility(appstyle.value == 1) {
                            Column {
                                appList.forEachIndexed { index, appInfo ->
                                    val notInstall = rememberSaveable { mutableStateOf(false) }
                                    FunctionApp(
                                        packageName = appInfo.packageName,
                                        activityName = appInfo.activityName,
                                        navController = navController
                                    ) { result ->
                                        if (result == "noapp") {
                                            if (!notInstallList.value.contains(appInfo.packageName)) {
                                                notInstallList.value += appInfo.packageName
                                            }
                                            notInstall.value = true
                                        }
                                    }
                                    if (index < appList.size - 1 && !notInstall.value) {
                                        addline()
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()))
                }
            }
        }
    }
}

data class AppInfo(val packageName: String, val activityName: String)

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
fun FunctionApp(packageName: String, activityName: String, navController: NavController, onResult: (String) -> Unit) {
    GetAppIconAndName(packageName = packageName) { appName, icon ->
        if (appName != "noapp") {
            val defaultColor = MiuixTheme.colorScheme.surface
            val noModuleActive = Color.Red

            // 使用 remember 缓存 dominantColor 的状态
            val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
            val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

            // 使用 LaunchedEffect 在 icon 或 dominantColor 变化时启动协程
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
                    color = dominantColor.value,
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
            onResult("noapp")
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

            // 使用 remember 缓存 dominantColor 的状态
            val dominantColor = remember { mutableStateOf(colorCache[packageName] ?: defaultColor) }
            val isLoading = remember { mutableStateOf(dominantColor.value == defaultColor) }

            // 使用 LaunchedEffect 在 icon 或 dominantColor 变化时启动协程
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
                    .width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    color = dominantColor.value,
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
                    maxLines = 1, // 限制为单行
                    overflow = TextOverflow.Ellipsis, // 超出部分显示省略号
                    softWrap = false, // 禁止自动换行
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                )
            }
        } else {
            onResult("noapp")
        }
    }
}

// 全局颜色缓存
internal val colorCache = mutableMapOf<String, Color>()

// 获取主色调的函数
suspend fun getAutoColor(icon: ImageBitmap): Color {
    return withContext(Dispatchers.IO) {
        val bitmap = icon.asAndroidBitmap()
        Palette.from(bitmap).generate().dominantSwatch?.rgb?.let { Color(it) } ?: Color.White
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
        else if (leadingIcon == null) Modifier
            .padding(start = insideMargin.width)
            .padding(vertical = insideMargin.height)
        else if (trailingIcon == null) Modifier
            .padding(end = insideMargin.width)
            .padding(vertical = insideMargin.height)
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
