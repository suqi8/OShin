package com.suqi8.oshin.ui.activity.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowUpDownIntegrated
import top.yukonga.miuix.kmp.icon.icons.basic.Check
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.PopupLayout
import top.yukonga.miuix.kmp.utils.getWindowSize
import kotlin.math.min

@Composable
fun funDropdown(
    title: String,
    summary: String?,
    selectedIndex: Int,
    options: List<String>,
    onSelectedIndexChange: (Int) -> Unit
) {
    SuperDropdown(
        title = title,
        summary = summary,
        items = options,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = onSelectedIndexChange
    )
}

/**
 * A dropdown with a title and a summary.
 *
 * @param items The options of the [SuperDropdown].
 * @param selectedIndex The index of the selected option.
 * @param title The title of the [SuperDropdown].
 * @param titleColor The color of the title.
 * @param summary The summary of the [SuperDropdown].
 * @param summaryColor The color of the summary.
 * @param dropdownColors The [DropdownColors] of the [SuperDropdown].
 * @param mode The dropdown show mode of the [SuperDropdown].
 * @param modifier The modifier to be applied to the [SuperDropdown].
 * @param insideMargin The margin inside the [SuperDropdown].
 * @param maxHeight The maximum height of the [ListPopup].
 * @param enabled Whether the [SuperDropdown] is enabled.
 * @param showValue Whether to show the selected value of the [SuperDropdown].
 * @param onClick The callback when the [SuperDropdown] is clicked.
 * @param onSelectedIndexChange The callback when the selected index of the [SuperDropdown] is changed.
 */
@Composable
fun SuperDropdown(
    items: List<String>,
    selectedIndex: Int,
    title: String,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    mode: DropDownMode = DropDownMode.Normal,
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    onClick: (() -> Unit)? = null,
    onSelectedIndexChange: ((Int) -> Unit)?,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val itemsNotEmpty = items.isNotEmpty()
    val actualEnabled = enabled && itemsNotEmpty

    val actionColor = if (actualEnabled) {
        MiuixTheme.colorScheme.onSurfaceVariantActions
    } else {
        MiuixTheme.colorScheme.disabledOnSecondaryVariant
    }

    var alignLeft by rememberSaveable { mutableStateOf(true) }

    val componentModifier = modifier.pointerInput(actualEnabled) {
        if (!actualEnabled) return@pointerInput
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.type != PointerEventType.Move) {
                    val eventChange = event.changes.first()
                    if (eventChange.pressed) {
                        alignLeft = eventChange.position.x < (size.width / 2)
                    }
                }
            }
        }
    }

    val handleClick: () -> Unit = {
        if (actualEnabled) {
            onClick?.invoke()
            isDropdownExpanded.value = !isDropdownExpanded.value
            if (isDropdownExpanded.value) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
            }
        }
    }

    BasicComponent(
        modifier = componentModifier,
        interactionSource = interactionSource,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        leftAction = if (itemsNotEmpty) {
            {
                SuperDropdownPopup(
                    items = items,
                    selectedIndex = selectedIndex,
                    isDropdownExpanded = isDropdownExpanded,
                    mode = mode,
                    alignLeft = alignLeft,
                    maxHeight = maxHeight,
                    dropdownColors = dropdownColors,
                    hapticFeedback = hapticFeedback,
                    onSelectedIndexChange = onSelectedIndexChange
                )
            }
        } else null,
        rightActions = {
            SuperDropdownRightActions(
                showValue = showValue,
                itemsNotEmpty = itemsNotEmpty,
                items = items,
                selectedIndex = selectedIndex,
                actionColor = actionColor
            )
        },
        onClick = handleClick,
        holdDownState = isDropdownExpanded.value,
        enabled = actualEnabled
    )
}

@Composable
private fun SuperDropdownPopup(
    items: List<String>,
    selectedIndex: Int,
    isDropdownExpanded: MutableState<Boolean>,
    mode: DropDownMode,
    alignLeft: Boolean,
    maxHeight: Dp?,
    dropdownColors: DropdownColors,
    hapticFeedback: HapticFeedback,
    onSelectedIndexChange: ((Int) -> Unit)?
) {
    ListPopup(
        show = isDropdownExpanded,
        alignment = if (mode == DropDownMode.AlwaysOnRight || !alignLeft) {
            PopupPositionProvider.Align.Right
        } else {
            PopupPositionProvider.Align.Left
        },
        onDismissRequest = {
            isDropdownExpanded.value = false
        },
        maxHeight = maxHeight
    ) {
        ListPopupColumn {
            items.forEachIndexed { index, string ->
                val dividerColor = MiuixTheme.colorScheme.dividerLine.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier.drawWithContent {
                        drawContent()

                        if (index < items.size - 1) {
                            val thicknessPx = 0.5.dp.toPx()
                            drawLine(
                                color = dividerColor,
                                start = Offset(x = 16.dp.toPx(), y = size.height - thicknessPx / 2),
                                end = Offset(x = size.width - 16.dp.toPx(), y = size.height - thicknessPx / 2),
                                strokeWidth = thicknessPx
                            )
                        }
                    }
                ) {
                    // 3. Box 里面是我们原始的、完全没有被修改过的 DropdownImpl。
                    DropdownImpl(
                        text = string,
                        optionSize = items.size,
                        isSelected = selectedIndex == index,
                        dropdownColors = dropdownColors,
                        onSelectedIndexChange = { selectedIdx ->
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            onSelectedIndexChange?.invoke(selectedIdx)
                            isDropdownExpanded.value = false
                        },
                        index = index
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.SuperDropdownRightActions(
    showValue: Boolean,
    itemsNotEmpty: Boolean,
    items: List<String>,
    selectedIndex: Int,
    actionColor: Color
) {
    if (showValue && itemsNotEmpty) {
        Text(
            modifier = Modifier.widthIn(max = 130.dp),
            text = items[selectedIndex],
            fontSize = MiuixTheme.textStyles.body2.fontSize,
            color = actionColor,
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }

    Image(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(10.dp, 16.dp)
            .align(Alignment.CenterVertically),
        imageVector = MiuixIcons.Basic.ArrowUpDownIntegrated,
        colorFilter = ColorFilter.tint(actionColor),
        contentDescription = null
    )
}

/**
 * The implementation of the dropdown.
 *
 * @param text The text of the current option.
 * @param optionSize The size of the options.
 * @param isSelected Whether the option is selected.
 * @param index The index of the current option in the options.
 * @param onSelectedIndexChange The callback when the index is selected.
 */
@Composable
fun DropdownImpl(
    text: String,
    optionSize: Int,
    isSelected: Boolean,
    index: Int,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    onSelectedIndexChange: (Int) -> Unit
) {
    val additionalTopPadding = if (index == 0) 16.dp else 12.dp
    val additionalBottomPadding = if (index == optionSize - 1) 16.dp else 12.dp

    val (textColor, backgroundColor) = if (isSelected) {
        dropdownColors.selectedContentColor to dropdownColors.containerColor
    } else {
        dropdownColors.contentColor to dropdownColors.containerColor
    }

    val checkColor = if (isSelected) {
        dropdownColors.selectedContentColor
    } else {
        Color.Transparent
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .widthIn(min = 178.dp)
            .clickable { onSelectedIndexChange(index) }
            .background(backgroundColor)
            .padding(horizontal = 16.dp)
            .padding(
                top = additionalTopPadding,
                bottom = additionalBottomPadding
            )
    ) {
        Text(
            modifier = Modifier.widthIn(max = 178.dp),
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = textColor,
        )

        Image(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(20.dp),
            imageVector = MiuixIcons.Basic.Check,
            colorFilter = BlendModeColorFilter(checkColor, BlendMode.SrcIn),
            contentDescription = null,
        )
    }
}

@Immutable
class DropdownColors(
    val contentColor: Color,
    val containerColor: Color,
    val selectedContentColor: Color,
    val selectedContainerColor: Color
)

object DropdownDefaults {

    @Composable
    fun dropdownColors(
        contentColor: Color = MiuixTheme.colorScheme.onSurface,
        containerColor: Color = MiuixTheme.colorScheme.surface,
        selectedContentColor: Color = MiuixTheme.colorScheme.onTertiaryContainer,
        selectedContainerColor: Color = MiuixTheme.colorScheme.tertiaryContainer
    ): DropdownColors {
        return DropdownColors(
            contentColor = contentColor,
            containerColor = containerColor,
            selectedContentColor = selectedContentColor,
            selectedContainerColor = selectedContainerColor
        )
    }
}

/**
 * The dropdown show mode.
 */
enum class DropDownMode {
    Normal,
    AlwaysOnRight
}

/**
 * A popup with a list of items.
 *
 * @param show The show state of the [ListPopup].
 * @param popupModifier The modifier to be applied to the [ListPopup].
 * @param popupPositionProvider The [PopupPositionProvider] of the [ListPopup].
 * @param alignment The alignment of the [ListPopup].
 * @param enableWindowDim Whether to enable window dimming when the [ListPopup] is shown.
 * @param shadowElevation The elevation of the shadow of the [ListPopup].
 * @param onDismissRequest The callback when the [ListPopup] is dismissed.
 * @param maxHeight The maximum height of the [ListPopup]. If null, the height will be calculated automatically.
 * @param minWidth The minimum width of the [ListPopup].
 * @param content The [Composable] content of the [ListPopup]. You should use the [ListPopupColumn] in general.
 */
@Composable
fun ListPopup(
    show: MutableState<Boolean>,
    popupModifier: Modifier = Modifier,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.Right,
    enableWindowDim: Boolean = false,
    shadowElevation: Dp = 24.dp,
    onDismissRequest: (() -> Unit)? = null,
    maxHeight: Dp? = null,
    minWidth: Dp = 178.dp,
    content: @Composable () -> Unit
) {
    if (!show.value) return

    val windowSize by rememberUpdatedState(getWindowSize())
    var parentBounds by remember { mutableStateOf(IntRect.Zero) }

    Layout(
        modifier = Modifier
            .onGloballyPositioned { childCoordinates ->
                childCoordinates.parentLayoutCoordinates?.let { parentLayoutCoordinates ->
                    val positionInWindow = parentLayoutCoordinates.positionInWindow()
                    parentBounds = IntRect(
                        left = positionInWindow.x.toInt(),
                        top = positionInWindow.y.toInt(),
                        right = positionInWindow.x.toInt() + parentLayoutCoordinates.size.width,
                        bottom = positionInWindow.y.toInt() + parentLayoutCoordinates.size.height
                    )
                }
            }
    ) { _, _ -> layout(0, 0) {} }
    if (parentBounds == IntRect.Zero) return

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val displayCutout = WindowInsets.displayCutout.asPaddingValues()
    val statusBars = WindowInsets.statusBars.asPaddingValues()
    val navigationBars = WindowInsets.navigationBars.asPaddingValues()
    val captionBar = WindowInsets.captionBar.asPaddingValues()

    val popupMargin = remember(windowSize, density) {
        with(density) {
            IntRect(
                left = popupPositionProvider.getMargins().calculateLeftPadding(layoutDirection).roundToPx(),
                top = popupPositionProvider.getMargins().calculateTopPadding().roundToPx(),
                right = popupPositionProvider.getMargins().calculateRightPadding(layoutDirection).roundToPx(),
                bottom = popupPositionProvider.getMargins().calculateBottomPadding().roundToPx()
            )
        }
    }

    val windowBounds = remember(windowSize, density) {
        with(density) {
            IntRect(
                left = displayCutout.calculateLeftPadding(layoutDirection).roundToPx(),
                top = statusBars.calculateTopPadding().roundToPx(),
                right = windowSize.width - displayCutout.calculateRightPadding(layoutDirection).roundToPx(),
                bottom = windowSize.height - navigationBars.calculateBottomPadding()
                    .roundToPx() - captionBar.calculateBottomPadding().roundToPx()
            )
        }
    }

    val transformOrigin = remember(windowSize, alignment, density) {
        val xInWindow = when (alignment) {
            PopupPositionProvider.Align.Right,
            PopupPositionProvider.Align.TopRight,
            PopupPositionProvider.Align.BottomRight -> parentBounds.right - popupMargin.right - with(density) { 64.dp.roundToPx() }

            else -> parentBounds.left + popupMargin.left + with(density) { 64.dp.roundToPx() }
        }
        val yInWindow = parentBounds.top + parentBounds.height / 2 - with(density) { 56.dp.roundToPx() }
        safeTransformOrigin(
            xInWindow / windowSize.width.toFloat(),
            yInWindow / windowSize.height.toFloat()
        )
    }

    PopupLayout(
        visible = show,
        enableWindowDim = enableWindowDim,
        transformOrigin = { transformOrigin },
    ) {
        val shape = remember { G2RoundedCornerShape(12.dp) }
        val elevationPx = with(density) { shadowElevation.toPx() }

        Box(
            modifier = popupModifier
                .pointerInput(onDismissRequest) {
                    detectTapGestures(
                        onTap = { onDismissRequest?.invoke() }
                    )
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            minWidth = if (minWidth.roundToPx() <= windowSize.width) minWidth.roundToPx() else windowSize.width,
                            minHeight = if (50.dp.roundToPx() <= windowSize.height) 50.dp.roundToPx() else windowSize.height,
                            maxHeight = maxHeight?.roundToPx()?.coerceAtLeast(50.dp.roundToPx())
                                ?: (windowBounds.height - popupMargin.top - popupMargin.bottom).coerceAtLeast(
                                    50.dp.roundToPx()
                                ),
                            maxWidth = if (minWidth.roundToPx() <= windowSize.width) windowSize.width else minWidth.roundToPx()
                        )
                    )
                    val measuredSize = IntSize(placeable.width, placeable.height)

                    val calculatedOffset = popupPositionProvider.calculatePosition(
                        parentBounds,
                        windowBounds,
                        layoutDirection,
                        measuredSize,
                        popupMargin,
                        alignment
                    )

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        placeable.place(calculatedOffset)
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        clip = true,
                        shape = shape,
                        shadowElevation = elevationPx,
                        ambientShadowColor = MiuixTheme.colorScheme.windowDimming,
                        spotShadowColor = MiuixTheme.colorScheme.windowDimming
                    )
                    .background(MiuixTheme.colorScheme.surface)
            ) {
                content()
            }
        }
    }

    BackHandler(enabled = show.value) {
        onDismissRequest?.invoke()
    }
}

/**
 * A column that automatically aligns the width to the widest item
 * @param content The items
 */
@Composable
fun ListPopupColumn(
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentContent by rememberUpdatedState(content)

    SubcomposeLayout(
        modifier = Modifier.verticalScroll(scrollState)
    ) { constraints ->
        var listHeight = 0
        val tempConstraints = constraints.copy(minWidth = 178.dp.roundToPx(), maxWidth = 288.dp.roundToPx(), minHeight = 0)

        // Measure pass to find the widest item
        val listWidth = subcompose("miuixPopupListFake", currentContent).map {
            it.measure(tempConstraints)
        }.maxOfOrNull { it.width }?.coerceIn(178.dp.roundToPx(), 288.dp.roundToPx()) ?: 178.dp.roundToPx()

        val childConstraints = constraints.copy(minWidth = listWidth, maxWidth = listWidth, minHeight = 0)

        // Actual measure and layout pass
        val placeables = subcompose("miuixPopupListReal", currentContent).map {
            val placeable = it.measure(childConstraints)
            listHeight += placeable.height
            placeable
        }
        layout(listWidth, min(constraints.maxHeight, listHeight)) {
            var currentY = 0
            placeables.forEach {
                it.place(0, currentY)
                currentY += it.height
            }
        }
    }
}

interface PopupPositionProvider {
    /**
     * Calculate the position (offset) of Popup
     *
     * @param anchorBounds Bounds of the anchored (parent) component
     * @param windowBounds Bounds of the safe area of window (excluding the [WindowInsets.Companion.statusBars], [WindowInsets.Companion.navigationBars] and [WindowInsets.Companion.captionBar])
     * @param layoutDirection [LayoutDirection]
     * @param popupContentSize Actual size of the popup content
     * @param popupMargin (Extra) Margins for the popup content. See [PopupPositionProvider.getMargins]
     * @param alignment Alignment of the popup (relative to the window). See [PopupPositionProvider.Align]
     */
    fun calculatePosition(
        anchorBounds: IntRect,
        windowBounds: IntRect,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
        popupMargin: IntRect,
        alignment: Align
    ): IntOffset

    /**
     * (Extra) Margins for the popup content.
     */
    fun getMargins(): PaddingValues

    /**
     * Position relative to the window, not relative to the anchor!
     */
    enum class Align {
        Left,
        Right,
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight
    }
}

object ListPopupDefaults {
    val DropdownPositionProvider = object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align
        ): IntOffset {
            val offsetX = if (alignment == PopupPositionProvider.Align.Right) {
                anchorBounds.right - popupContentSize.width - popupMargin.right
            } else {
                anchorBounds.left + popupMargin.left
            }
            val offsetY = if (windowBounds.bottom - anchorBounds.bottom > popupContentSize.height) {
                // Show below
                anchorBounds.bottom + popupMargin.bottom
            } else if (anchorBounds.top - windowBounds.top > popupContentSize.height) {
                // Show above
                anchorBounds.top - popupContentSize.height - popupMargin.top
            } else {
                // Middle
                anchorBounds.top + anchorBounds.height / 2 - popupContentSize.height / 2
            }
            return IntOffset(
                x = offsetX.coerceIn(
                    windowBounds.left,
                    (windowBounds.right - popupContentSize.width - popupMargin.right).coerceAtLeast(windowBounds.left)
                ),
                y = offsetY.coerceIn(
                    (windowBounds.top + popupMargin.top).coerceAtMost(windowBounds.bottom - popupContentSize.height - popupMargin.bottom),
                    windowBounds.bottom - popupContentSize.height - popupMargin.bottom
                )
            )
        }

        override fun getMargins(): PaddingValues {
            return PaddingValues(horizontal = 0.dp, vertical = 8.dp)
        }
    }
    val ContextMenuPositionProvider = object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align
        ): IntOffset {
            val offsetX: Int
            val offsetY: Int
            when (alignment) {
                PopupPositionProvider.Align.TopLeft -> {
                    offsetX = anchorBounds.left + popupMargin.left
                    offsetY = anchorBounds.bottom + popupMargin.top
                }

                PopupPositionProvider.Align.TopRight -> {
                    offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                    offsetY = anchorBounds.bottom + popupMargin.top
                }

                PopupPositionProvider.Align.BottomLeft -> {
                    offsetX = anchorBounds.left + popupMargin.left
                    offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                }

                PopupPositionProvider.Align.BottomRight -> {
                    offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                    offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                }

                else -> {
                    // Fallback
                    offsetX = if (alignment == PopupPositionProvider.Align.Right) {
                        anchorBounds.right - popupContentSize.width - popupMargin.right
                    } else {
                        anchorBounds.left + popupMargin.left
                    }
                    offsetY = if (windowBounds.bottom - anchorBounds.bottom > popupContentSize.height) {
                        // Show below
                        anchorBounds.bottom + popupMargin.bottom
                    } else if (anchorBounds.top - windowBounds.top > popupContentSize.height) {
                        // Show above
                        anchorBounds.top - popupContentSize.height - popupMargin.top
                    } else {
                        // Middle
                        anchorBounds.top + anchorBounds.height / 2 - popupContentSize.height / 2
                    }
                }
            }
            return IntOffset(
                x = offsetX.coerceIn(
                    windowBounds.left,
                    (windowBounds.right - popupContentSize.width - popupMargin.right).coerceAtLeast(windowBounds.left)
                ),
                y = offsetY.coerceIn(
                    (windowBounds.top + popupMargin.top).coerceAtMost(windowBounds.bottom - popupContentSize.height - popupMargin.bottom),
                    windowBounds.bottom - popupContentSize.height - popupMargin.bottom
                )
            )
        }

        override fun getMargins(): PaddingValues {
            return PaddingValues(horizontal = 20.dp, vertical = 0.dp)
        }
    }
}

/**
 * Ensure TransformOrigin is available.
 */
fun safeTransformOrigin(x: Float, y: Float): TransformOrigin {
    val safeX = if (x.isNaN() || x < 0f) 0f else x
    val safeY = if (y.isNaN() || y < 0f) 0f else y
    return TransformOrigin(safeX, safeY)
}
