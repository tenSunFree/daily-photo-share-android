package com.sun.daily_photo_share_android.core.designsystem.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme

/** Secondary action button. Use [DailyButton] for the primary action of a screen. */
@Composable
fun DailyTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = DailyTheme.sizes.minTouchTarget),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(DailyTheme.spacing.sm))
        }
        Text(text)
    }
}