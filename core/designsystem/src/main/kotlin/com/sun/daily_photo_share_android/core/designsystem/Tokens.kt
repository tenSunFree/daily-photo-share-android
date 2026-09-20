package com.sun.daily_photo_share_android.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class DailySpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

@Immutable
data class DailySizes(
    val minTouchTarget: Dp = 48.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val selectionBadge: Dp = 24.dp,
    val photoGridMinCell: Dp = 110.dp,
)

internal val LocalDailySpacing = staticCompositionLocalOf { DailySpacing() }
internal val LocalDailySizes = staticCompositionLocalOf { DailySizes() }