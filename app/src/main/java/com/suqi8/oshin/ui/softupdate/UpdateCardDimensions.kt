package com.suqi8.oshin.ui.softupdate

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

data class UpdateCardDimensions(
    val cardHeight: Dp,
    val logoTopPadding: Dp,
    val logoHeight: Dp,
    val logoWidth: Dp,
    val titleFontSize: TextUnit,
    val titleTopPadding: Dp,
    val phoneNameFontSize: TextUnit,
    val phoneNameTopPadding: Dp,
    val statusFontSize: TextUnit
)
