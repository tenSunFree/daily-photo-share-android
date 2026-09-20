package com.sun.daily_photo_share_android.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF006A6A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9CF1F0),
    secondary = Color(0xFF4A6363),
    tertiary = Color(0xFF4B607C),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF80D5D4),
    onPrimary = Color(0xFF003737),
    primaryContainer = Color(0xFF004F4F),
    secondary = Color(0xFFB0CCCB),
    tertiary = Color(0xFFB3C8E8),
)

private val DailyTypography = Typography()

private val DailyShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp),
)

/**
 * Project-specific entry point for semantic tokens.
 * Colors, typography, and shapes inherit from MaterialTheme.*; spacing and dimensions use DailyTheme.spacing / DailyTheme.sizes.
 */
object DailyTheme {
    val spacing: DailySpacing
        @Composable @ReadOnlyComposable get() = LocalDailySpacing.current
    val sizes: DailySizes
        @Composable @ReadOnlyComposable get() = LocalDailySizes.current
}

@Composable
fun DailyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }
    CompositionLocalProvider(
        LocalDailySpacing provides DailySpacing(),
        LocalDailySizes provides DailySizes(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = DailyTypography,
            shapes = DailyShapes,
            content = content,
        )
    }
}