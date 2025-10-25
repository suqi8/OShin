package com.suqi8.oshin.ui.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.suqi8.oshin.R
import com.suqi8.oshin.ui.main.CarouselItem
import com.suqi8.oshin.ui.main.DeviceInfo
import com.suqi8.oshin.ui.main.FridaStatus
import com.suqi8.oshin.ui.main.HomeViewModel
import com.suqi8.oshin.ui.main.ModuleStatus
import com.suqi8.oshin.ui.main.RootStatus
import com.suqi8.oshin.ui.main.Status
import com.suqi8.oshin.ui.module.SearchableItem
import com.suqi8.oshin.utils.GetFuncRoute
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import kotlin.math.abs

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainHome(
    padding: PaddingValues,
    topAppBarScrollBehavior: ScrollBehavior,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MiuixTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .scrollEndHaptic()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
            // 轮播图
            item {
                uiState.carouselItems?.let {
                    FeaturedCollectionsSection(items = it)
                }
            }

            // 状态面板
            item {
                ModernDashboardSection(
                    moduleStatus = uiState.moduleStatus,
                    rootStatus = uiState.rootStatus,
                    fridaStatus = uiState.fridaStatus
                )
            }

            // 今日亮点（推荐功能）
            if (uiState.randomFeatures.isNotEmpty()) {
                item {
                    TodayHighlightsSection(
                        features = uiState.randomFeatures,
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }

            // 设备信息
            item {
                uiState.deviceInfo?.let { DeviceInfoSection(info = it) }
            }

            // 官方频道
            item {
                OfficialChannelCard(
                    navController = navController,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}

@Composable
fun ModernSectionTitle(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MiuixTheme.colorScheme.onBackground
        )
        subtitle?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedCollectionsSection(items: List<CarouselItem>) {
    val pagerState = rememberPagerState { items.size }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(durationMillis = 800))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        ModernSectionTitle(
            title = stringResource(id = R.string.section_title_featured),
            subtitle = stringResource(id = R.string.section_subtitle_featured)
        )

        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 16.dp
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = lerp(1f, 0.88f, abs(pageOffset).coerceAtMost(1f))
            val alpha = lerp(1f, 0.6f, abs(pageOffset).coerceAtMost(1f))

            val item = items[page]

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6),
                                Color(0xFFEC4899)
                            )
                        )
                    )
                    .clickable(enabled = item.actionUrl != null) {
                        item.actionUrl?.let { url ->
                            try {
                                uriHandler.openUri(url)
                            } catch (e: Exception) {
                                // 如果URI处理器失败，可以尝试其他方式
                                // 这里可以添加自定义的Intent跳转逻辑
                            }
                        }
                    }
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 只有在有标题或简介时才显示渐变遮罩和内容
                if (item.title != null || item.description != null) {
                    // 渐变遮罩
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // 内容
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        item.title?.let {
                            Text(
                                text = it,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        item.description?.let {
                            Text(
                                text = it,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // 可点击提示 - 只有在有链接时显示
                if (item.actionUrl != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.9f),
                                        Color.White.copy(alpha = 0.7f)
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "可点击",
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 页面指示器
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val width by animateFloatAsState(
                    targetValue = if (isSelected) 24f else 8f,
                    animationSpec = tween(300)
                )
                val color = if (isSelected)
                    MiuixTheme.colorScheme.primary
                else
                    MiuixTheme.colorScheme.onBackground.copy(alpha = 0.3f)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun ModernDashboardSection(
    moduleStatus: ModuleStatus,
    rootStatus: RootStatus,
    fridaStatus: FridaStatus
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ModernSectionTitle(
            title = stringResource(id = R.string.section_title_status)
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernStatusCard(
                modifier = Modifier.weight(1f),
                status = moduleStatus.status,
                icon = Icons.Default.VerifiedUser,
                title = stringResource(id = R.string.module_status),
                message = moduleStatus.message,
                gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
            )

            ModernStatusCard(
                modifier = Modifier.weight(1f),
                status = rootStatus.status,
                icon = Icons.Default.Security,
                title = stringResource(id = R.string.root_status),
                message = rootStatus.version,
                gradientColors = listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
            )
        }
    }
}

@Composable
fun ModernStatusCard(
    modifier: Modifier = Modifier,
    status: Status,
    icon: ImageVector,
    title: String,
    message: String,
    gradientColors: List<Color>
) {
    val statusColor = when (status) {
        Status.SUCCESS -> Color(0xFF22C55E)
        Status.ERROR -> Color(0xFFEF4444)
        Status.WARNING -> Color(0xFFF59E0B)
        Status.LOADING -> MiuixTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                ModernStatusIndicator(status = status, color = statusColor)
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = message,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ModernStatusIndicator(status: Status, color: Color) {
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by if (status == Status.LOADING) {
        infiniteTransition.animateFloat(
            0.3f, 1f,
            infiniteRepeatable(tween(800), RepeatMode.Reverse)
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = alpha))
    )
}


// 今日亮点 - 展示推荐功能 (苹果风格)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TodayHighlightsSection(
    features: List<SearchableItem>,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ModernSectionTitle(
            title = stringResource(id = R.string.section_title_highlights),
            subtitle = stringResource(id = R.string.section_subtitle_highlights)
        )

        Spacer(Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(features) { feature ->
                with(sharedTransitionScope) {
                    AppleStyleFeatureCard(
                        feature = feature,
                        onClick = {
                            navController.navigate("${feature.route}?highlightKey=${feature.key}")
                        },
                        sharedTransitionScope = this,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ModernFeatureCard(
    feature: SearchableItem,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val routeId = feature.route.substringAfter("feature/")
    val route = remember(routeId, context) { GetFuncRoute(routeId, context) }
    
    // 为不同的功能分配不同的主题色
    val themeColor = remember(feature.key) {
        val colors = listOf(
            Color(0xFF6366F1), // 靛蓝
            Color(0xFFEC4899), // 粉红
            Color(0xFF10B981), // 绿色
            Color(0xFFF59E0B), // 橙色
            Color(0xFF8B5CF6), // 紫色
            Color(0xFF14B8A6), // 青色
        )
        colors[kotlin.math.abs(feature.key.hashCode()) % colors.size]
    }
    
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(160.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = feature.key),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(RoundedCornerShape(16.dp))
                .background(MiuixTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = themeColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 标题和图标
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(themeColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = feature.title.first().toString(),
                                color = themeColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = feature.title,
                            color = MiuixTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // 简介信息 - 固定行数，统一高度
                    Text(
                        text = if (feature.summary.isNotEmpty()) feature.summary else "暂无描述",
                        color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 2,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // 底部路由信息
                if (route.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(themeColor)
                        )
                        Text(
                            text = route,
                            color = themeColor,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // 即使没有路由信息也保持占位高度
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppleStyleFeatureCard(
    feature: SearchableItem,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    ModernFeatureCard(feature, onClick, sharedTransitionScope, animatedVisibilityScope)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CompactFeatureCard(
    feature: SearchableItem,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val routeId = feature.route.substringAfter("feature/")
    val route = remember(routeId, context) { GetFuncRoute(routeId, context) }
    
    // 为不同的功能分配不同的主题色
    val themeColor = remember(feature.key) {
        val colors = listOf(
            Color(0xFF6366F1), // 靛蓝
            Color(0xFFEC4899), // 粉红
            Color(0xFF10B981), // 绿色
            Color(0xFFF59E0B), // 橙色
            Color(0xFF8B5CF6), // 紫色
            Color(0xFF14B8A6), // 青色
        )
        colors[kotlin.math.abs(feature.key.hashCode()) % colors.size]
    }
    
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(100.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = feature.key),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(RoundedCornerShape(12.dp))
                .background(MiuixTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = themeColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(themeColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feature.title.first().toString(),
                            color = themeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        text = feature.title,
                        color = MiuixTheme.colorScheme.onBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                if (feature.summary.isNotEmpty()) {
                    Text(
                        text = feature.summary,
                        color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(themeColor)
                    )
                    Text(
                        text = route,
                        color = themeColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = themeColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HighlightFeatureCard(
    feature: SearchableItem,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val routeId = feature.route.substringAfter("feature/")
    val summaryWithRoute = remember(feature.summary, routeId) {
        val route = GetFuncRoute(routeId, context)
        (feature.summary) + if (route.isNotEmpty() && feature.summary.isNotEmpty()) "\n$route" else route
    }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = feature.key),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(RoundedCornerShape(16.dp))
                .background(MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feature.title,
                        color = MiuixTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (summaryWithRoute.isNotEmpty()) {
                        Text(
                            text = summaryWithRoute,
                            color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// 设备信息 - 混合风格设计
@Composable
fun DeviceInfoSection(info: DeviceInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ModernSectionTitle(
            title = stringResource(id = R.string.section_title_device_info)
        )

        Spacer(Modifier.height(16.dp))

        // 电池状态卡片 - 渐变背景
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            when {
                                info.batteryHealthPercent >= 80 -> Color(0xFF10B981)
                                info.batteryHealthPercent >= 60 -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            },
                            when {
                                info.batteryHealthPercent >= 80 -> Color(0xFF059669)
                                info.batteryHealthPercent >= 60 -> Color(0xFFDC2626)
                                else -> Color(0xFFB91C1C)
                            }
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.battery_status),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (info.chipSoc > 0) "${stringResource(R.string.real_battery_level)} ${info.chipSoc}%" else "${stringResource(R.string.gauge_title_health)} ${"%.1f".format(info.batteryHealthPercent)}%",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${info.currentCapacity}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "mAh",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BatteryMetricItem("循环次数", "${info.cycleCount}次")
                    BatteryMetricItem("设计容量", "${info.designCapacity}mAh")
                    BatteryMetricItem("系统健康", info.batteryHealthDisplay)
                }
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.text_info_calculated_health),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${"%.1f".format(info.calculatedHealth)}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "系统健康度",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${"%.1f".format(info.batteryHealthPercent)}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 系统信息卡片 - 简洁风格
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MiuixTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { /* 系统信息卡片点击事件 */ }
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 标题行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Text(
                        text = "系统信息",
                        color = MiuixTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // 信息列表
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SystemInfoRow(stringResource(R.string.info_region), info.country)
                    SystemInfoRow(stringResource(R.string.info_android), "${info.androidVersion} (API ${info.sdkVersion})")
                    SystemInfoRow(stringResource(R.string.info_system), info.systemVersion)
                    SystemInfoRow(stringResource(R.string.battery_status), "${info.batteryHealthDisplay} (${info.batteryHealthRaw})")
                }
            }
        }
    }
}

// 电池指标项
@Composable
fun BatteryMetricItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}

// 系统信息行 - 简洁的左右布局
@Composable
fun SystemInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = MiuixTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

// 系统信息卡片 - 用于渐变背景卡片内
@Composable
fun SystemInfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Text(
                text = value,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// 系统信息项 - 保留备用
@Composable
fun SystemInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 15.sp
        )
        Text(
            text = value,
            color = MiuixTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// 渐变信息卡片 - 类似系统状态卡片
@Composable
fun GradientInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                // 状态指示器
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// 大型渐变信息卡片 - 用于系统信息
@Composable
fun LargeGradientInfoCard(
    title: String,
    gradientColors: List<Color>,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            content()
        }
    }
}

// 渐变信息行 - 用于大卡片内的信息显示
@Composable
fun GradientInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
fun SimpleInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = title,
            color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun TextInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = MiuixTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoChip(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MiuixTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MiuixTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = MiuixTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun OfficialChannelCard(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "about_group"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                )
                .clickable { navController.navigate("about_group") }
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.group),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(32.dp)
                    )

                    Column {
                        Text(
                            text = stringResource(id = R.string.official_channel),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = stringResource(id = R.string.official_channel_subtitle),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}
