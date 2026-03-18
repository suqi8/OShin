package com.suqi8.oshin.ui.mainscreen.softupdate

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.factory.prefs
import com.suqi8.oshin.BuildConfig
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.activity.components.Card
import com.suqi8.oshin.ui.activity.components.FunPage
import com.suqi8.oshin.ui.activity.components.addline
import com.suqi8.oshin.ui.activity.components.LiquidButton
import com.suqi8.oshin.utils.BaseMarkdown
import com.suqi8.oshin.utils.formatDate
import com.suqi8.oshin.utils.formatTimeAgo
import com.suqi8.oshin.utils.getPhoneName
import com.suqi8.oshin.utils.markdownTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.ProgressIndicatorDefaults
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.other.GitHub
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import kotlin.math.roundToInt

// ============ 常量和配置 ============

private object UpdatePageDimens {
    val TabRowPadding = 100.dp
    val TabRowMinWidth = 0.dp
    val TabRowMaxWidth = 50.dp
    val TabRowHeight = 40.dp
    val TabRowCornerRadius = 20.dp

    val ContentStartPadding = 40.dp
    val ContentEndPadding = 40.dp
    val ContentTopPadding = 12.dp
    val ContentBottomPadding = 8.dp
    val ContentVerticalPadding = PaddingValues(
        start = ContentStartPadding,
        end = ContentEndPadding,
        top = ContentTopPadding,
        bottom = ContentBottomPadding
    )

    val CardCornerRadius = 20.dp
    val DownloadButtonHeight = 52.dp
    val DownloadButtonCornerRadius = 26.dp
    val DownloadButtonHorizontalPadding = 16.dp
    val DownloadButtonVerticalPadding = 12.dp

    val LazyColumnBottomPadding = 96.dp
}

// ============ 主页面 ============

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SoftwareUpdatePage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val TAG = "SoftwareUpdatePage"
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val viewModel: UpdateViewModel = hiltViewModel(activity)
    var releaseType by remember { mutableIntStateOf(viewModel.getSavedUpdateChannel()) }
    val isDebugEnabled = remember { context.prefs("settings").getBoolean("Debug", false) }
    val currentVersion = rememberCurrentVersion(context)
    val showTokenDialog = remember { mutableStateOf(false) }

    val installLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "安装 Activity 结果: ${result.resultCode}")
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(releaseType) {
        viewModel.fetchReleases(releaseType)
    }

    LaunchedEffect(Unit) {
        viewModel.checkTokenStatus()
    }

    // 计算实际状态
    val latestRelease = remember(viewModel.releases) { viewModel.releases.firstOrNull() }
    val latestApkAsset = remember(latestRelease) {
        latestRelease?.assets?.firstOrNull { it.name?.endsWith(".apk") == true }
    }

    val hasNewVersion = remember(latestRelease, currentVersion) {
        if (isDebugEnabled && viewModel.releases.isNotEmpty()) {
            true
        } else {
            viewModel.hasNewVersion(latestRelease, currentVersion, releaseType)
        }
    }

    val isDownloadActionEnabled = hasNewVersion && latestApkAsset?.downloadUrl != null

    LaunchedEffect(latestApkAsset, viewModel.isDownloading) {
        Log.d(TAG, "triggerAutoDownload: ${viewModel.triggerAutoDownload} latestApkAsset: $latestApkAsset, isDownloading: ${viewModel.isDownloading}")
        if (viewModel.triggerAutoDownload && latestApkAsset != null && !viewModel.isDownloading) {

            viewModel.consumeAutoDownloadFlag()

            Toast.makeText(context, context.getString(R.string.update_page_downloading), Toast.LENGTH_SHORT).show()

            scope.launch(Dispatchers.IO) {
                viewModel.error = null
                val file = viewModel.downloadApk(latestApkAsset.downloadUrl, context)
                withContext(Dispatchers.Main) {
                    if (file != null) {
                        Toast.makeText(context, context.getString(R.string.update_page_download_complete), Toast.LENGTH_SHORT).show()
                        installApk(context, file, installLauncher, scope)
                    } else {
                        val errorMsg = viewModel.error ?: ""
                        Toast.makeText(context, context.getString(R.string.update_page_download_failed, errorMsg), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    FunPage(
        title = stringResource(R.string.check_update),
        navController = navController,
        scrollBehavior = topAppBarScrollBehavior,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        animationKey = "update_card_transition",
        action = { backdrop ->
            LiquidButton(
                onClick = { showTokenDialog.value = true },
                modifier = Modifier.size(40.dp),
                backdrop = backdrop
            ) {
                Icon(
                    imageVector = MiuixIcons.Other.GitHub,
                    contentDescription = stringResource(R.string.update_page_github_token_desc),
                    tint = MiuixTheme.colorScheme.onBackground
                )
            }
        }
    ) { padding ->
        var showHistory by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .overScrollVertical()
                    .scrollEndHaptic()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(bottom = UpdatePageDimens.LazyColumnBottomPadding)
            ) {
                item {
                    Spacer(modifier = Modifier
                        .displayCutoutPadding()
                        .height(0.dp))
                }

                // TabRow
                item {
                    Box(
                        modifier = Modifier.padding(
                            horizontal = UpdatePageDimens.TabRowPadding,
                            vertical = 8.dp
                        )
                    ) {
                        TabRow(
                            tabs = listOf(
                                stringResource(R.string.update_page_tab_release),
                                stringResource(R.string.update_page_tab_ci)
                            ),
                            selectedTabIndex = releaseType,
                            minWidth = UpdatePageDimens.TabRowMinWidth,
                            maxWidth = UpdatePageDimens.TabRowMaxWidth,
                            cornerRadius = UpdatePageDimens.TabRowCornerRadius,
                            height = UpdatePageDimens.TabRowHeight,
                            onTabSelected = { releaseType = it }
                        )
                    }
                }

                // 统一的信息卡片
                item {
                    UnifiedUpdateInfoCard(
                        release = latestRelease,
                        releaseType = releaseType,
                        error = viewModel.error,
                        isLoading = viewModel.isLoading,
                        hasNewVersion = hasNewVersion
                    )
                }

                // 历史记录
                item {
                    UpdateInfoSection(
                        release = latestRelease,
                        hasNewVersion = hasNewVersion,
                        showHistory = showHistory,
                        onToggleHistory = { showHistory = !showHistory },
                        context = context // 传入 context
                    )
                }

                // 历史版本列表
                if (!hasNewVersion && showHistory) {
                    items(viewModel.releases) { release ->
                        ReleaseHistoryItem(release = release)
                    }
                }

                // 新版本详情
                item {
                    if (hasNewVersion && latestRelease != null) {
                        Column(modifier = Modifier.padding(UpdatePageDimens.ContentVerticalPadding)) {
                            addline()
                            Spacer(Modifier.height(20.dp))
                            BaseMarkdown(
                                content = latestRelease.body,
                                typography = markdownTypography()
                            )
                        }
                    }
                }
            }

            // 下载按钮
            if (hasNewVersion && latestApkAsset != null) {
                DownloadButtonSection(
                    isDownloading = viewModel.isDownloading,
                    downloadProgress = viewModel.downloadProgress.collectAsState().value,
                    isEnabled = isDownloadActionEnabled,
                    onDownloadClick = {
                        scope.launch(Dispatchers.IO) {
                            viewModel.error = null
                            val file = viewModel.downloadApk(latestApkAsset.downloadUrl, context)
                            withContext(Dispatchers.Main) {
                                if (file != null) {
                                    Toast.makeText(context, context.getString(R.string.update_page_download_complete), Toast.LENGTH_SHORT).show()
                                    installApk(context, file, installLauncher, scope)
                                } else {
                                    val errorMsg = viewModel.error ?: ""
                                    Toast.makeText(context, context.getString(R.string.update_page_download_failed, errorMsg), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                )
            }
        }

        // Token 输入对话框
        TokenEntryDialog(
            show = showTokenDialog,
            currentToken = viewModel.currentToken,
            onDismiss = { showTokenDialog.value = false },
            onSave = { token ->
                viewModel.saveToken(token)
                showTokenDialog.value = false
                scope.launch { viewModel.fetchReleases(releaseType) }
            },
            onClear = {
                viewModel.clearToken()
                showTokenDialog.value = false
                scope.launch { viewModel.fetchReleases(releaseType) }
            }
        )
    }
}

// ============ 子组件 ============

@Composable
private fun rememberCurrentVersion(context: Context): String {
    return remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"
        } catch (e: Exception) {
            "0.0.0"
        }
    }
}

@Composable
private fun UpdateInfoSection(
    release: GitHubRelease?,
    hasNewVersion: Boolean,
    showHistory: Boolean,
    onToggleHistory: () -> Unit,
    context: Context // 新增 Context 参数
) {
    Column(modifier = Modifier.padding(UpdatePageDimens.ContentVerticalPadding)) {
        Text(
            text = stringResource(R.string.update_page_software_version),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MiuixTheme.colorScheme.onSurface
        )

        if (!hasNewVersion) {
            Text(
                text = stringResource(R.string.update_page_current_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MiuixTheme.colorScheme.onBackgroundVariant
            )
            Text(
                text = if (showHistory) stringResource(R.string.update_page_hide_history) else stringResource(R.string.update_page_show_history),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onBackgroundVariant,
                modifier = Modifier.clickable { onToggleHistory() }
            )
        } else {
            release?.let {
                val totalSizeBytes = it.assets.sumOf { asset -> asset.size }
                val totalSizeMB = totalSizeBytes / 1024f / 1024f
                Text(
                    text = stringResource(R.string.update_page_release_size, it.name, totalSizeMB),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MiuixTheme.colorScheme.onBackgroundVariant
                )
            }
        }

        val dateToShow = release?.publishedAt ?: release?.createdAt

        dateToShow?.let { date ->
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.update_page_published_at, formatDate(date)),
                    fontSize = 11.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    // 传入 context
                    text = stringResource(R.string.update_page_time_ago_wrapper, formatTimeAgo(date, context)),
                    fontSize = 11.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

@Composable
private fun ReleaseHistoryItem(release: GitHubRelease) {
    Column(
        modifier = Modifier.padding(
            start = 40.dp,
            end = 40.dp,
            top = 12.dp,
            bottom = 8.dp
        )
    ) {
        addline()
        Spacer(Modifier.height(16.dp))
        Text(
            text = release.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MiuixTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BaseMarkdown(
            content = release.body,
            typography = markdownTypography()
        )
    }
}

@Composable
private fun DownloadButtonSection(
    isDownloading: Boolean,
    downloadProgress: Float,
    isEnabled: Boolean,
    onDownloadClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiuixTheme.colorScheme.background)
            .padding(
                horizontal = UpdatePageDimens.DownloadButtonHorizontalPadding,
                vertical = UpdatePageDimens.DownloadButtonVerticalPadding
            )
    ) {
        DownloadButton(
            isDownloading = isDownloading,
            downloadProgress = downloadProgress,
            isEnabled = isEnabled || isDownloading,
            onClick = onDownloadClick
        )
    }
}

@Composable
private fun DownloadButton(
    isDownloading: Boolean,
    downloadProgress: Float,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        insideMargin = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColorsPrimary(),
        modifier = Modifier
            .fillMaxWidth()
            .height(UpdatePageDimens.DownloadButtonHeight)
            .clip(RoundedCornerShape(UpdatePageDimens.DownloadButtonCornerRadius)),
        enabled = isEnabled
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 背景进度条
            if (isDownloading) {
                LinearProgressIndicator(
                    progress = if (downloadProgress >= 0f) downloadProgress else null,
                    height = UpdatePageDimens.DownloadButtonHeight
                )
            }

            // 前景文本
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val buttonText = if (isDownloading) {
                    val progressPercent = (downloadProgress * 100).roundToInt()
                    if (downloadProgress >= 0f)
                        stringResource(R.string.update_page_downloading_progress, progressPercent)
                    else
                        stringResource(R.string.update_page_downloading)
                } else {
                    stringResource(R.string.update_page_download_and_install)
                }

                Text(
                    text = buttonText,
                    color = MiuixTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ============ 统一信息卡片 ============

@SuppressLint("PrivateApi")
@Composable
private fun UnifiedUpdateInfoCard(
    release: GitHubRelease?,
    releaseType: Int,
    error: String? = null,
    isLoading: Boolean,
    hasNewVersion: Boolean
) {
    val cardDimensions = rememberUpdateCardDimensions(hasNewVersion)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Card(
                cornerRadius = UpdatePageDimens.CardCornerRadius,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardDimensions.cardHeight)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.updatebg),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.colorlogo),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = cardDimensions.logoTopPadding)
                                    .height(cardDimensions.logoHeight)
                                    .width(cardDimensions.logoWidth)
                            )

                            Text(
                                if (releaseType == 0)
                                    stringResource(R.string.update_page_card_title_release)
                                else
                                    stringResource(R.string.update_page_card_title_ci),
                                fontSize = cardDimensions.titleFontSize,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = cardDimensions.titleTopPadding)
                            )

                            val phoneName = getPhoneName()
                            Text(
                                phoneName,
                                fontSize = cardDimensions.phoneNameFontSize,
                                color = Color.White,
                                modifier = Modifier.padding(top = cardDimensions.phoneNameTopPadding)
                            )
                        }

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CardStatusRow(
                                    error = error,
                                    isLoading = isLoading,
                                    release = release,
                                    hasNewVersion = hasNewVersion,
                                    statusFontSize = cardDimensions.statusFontSize
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberUpdateCardDimensions(hasNewVersion: Boolean): UpdateCardDimensions {
    val animSpec = tween<Float>(durationMillis = 400)

    val shrinkFactor70 by animateFloatAsState(
        targetValue = if (!hasNewVersion) 1.0f else 0.8f,
        label = "ShrinkFactor70",
        animationSpec = animSpec
    )

    val shrinkFactor50 by animateFloatAsState(
        targetValue = if (!hasNewVersion) 1.0f else 0.6f,
        label = "ShrinkFactor50",
        animationSpec = animSpec
    )

    val baseCardHeight = 500.dp
    val baseLogoTopPadding = 100.dp
    val baseLogoHeight = 100.dp
    val baseLogoWidth = 128.dp
    val baseTitleFontSize = 24.sp
    val baseTitleTopPadding = 8.dp
    val basePhoneNameFontSize = 13.sp
    val basePhoneNameTopPadding = 4.dp
    val baseStatusFontSize = 18.sp

    return UpdateCardDimensions(
        cardHeight = baseCardHeight * shrinkFactor50,
        logoTopPadding = baseLogoTopPadding * shrinkFactor70,
        logoHeight = baseLogoHeight * shrinkFactor70,
        logoWidth = baseLogoWidth * shrinkFactor70,
        titleFontSize = baseTitleFontSize * shrinkFactor70,
        titleTopPadding = baseTitleTopPadding * shrinkFactor70,
        phoneNameFontSize = basePhoneNameFontSize * shrinkFactor70,
        phoneNameTopPadding = basePhoneNameTopPadding * shrinkFactor70,
        statusFontSize = baseStatusFontSize * shrinkFactor70
    )
}

@Composable
private fun CardStatusRow(
    error: String?,
    isLoading: Boolean,
    release: GitHubRelease?,
    hasNewVersion: Boolean,
    statusFontSize: TextUnit
) {
    Row(verticalAlignment = Alignment.Bottom) {
        val text = when {
            error != null -> error // 来自 ViewModel，已是多语言
            isLoading && release == null -> stringResource(R.string.update_page_status_checking)
            hasNewVersion -> stringResource(R.string.update_page_status_new_version)
            else -> stringResource(R.string.update_page_status_latest)
        }

        Crossfade(targetState = text, animationSpec = tween(durationMillis = 250)) { current ->
            Text(text = current, fontSize = statusFontSize, color = Color.White)
        }

        AnimatedVisibility(
            visible = isLoading && release == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator(
                colors = ProgressIndicatorDefaults.progressIndicatorColors(
                    foregroundColor = Color.White,
                    backgroundColor = Color.Transparent
                ),
                size = 14.dp,
                strokeWidth = 1.dp,
                modifier = Modifier.padding(start = 5.dp, bottom = 3.dp)
            )
        }
    }
}


// ============ Token 对话框 ============

@Composable
private fun TokenEntryDialog(
    show: MutableState<Boolean>,
    currentToken: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onClear: () -> Unit
) {
    var tokenInput by remember { mutableStateOf(currentToken ?: "") }

    SuperDialog(
        show = show,
        title = stringResource(R.string.token_dialog_title),
        onDismissRequest = onDismiss,
        summary = stringResource(R.string.token_dialog_summary)
    ) {
        TextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            label = stringResource(R.string.token_dialog_label),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            backgroundColor = MiuixTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                text = stringResource(R.string.ok),
                onClick = { onSave(tokenInput.trim()) },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                enabled = tokenInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            if (!currentToken.isNullOrBlank()) {
                TextButton(
                    text = stringResource(R.string.token_dialog_clear),
                    onClick = onClear,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }

            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
