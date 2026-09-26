package com.sun.daily_photo_share_android.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme

/**
 * Selection marker drawn over a photo: a filled circle with the 1-based share order when
 * selected, an empty outlined circle when the photo can be selected but is not.
 */
@Composable
fun DailySelectionBadge(order: Int?, modifier: Modifier = Modifier) {
    val badgeModifier = modifier.size(DailyTheme.sizes.selectionBadge)
    if (order != null) {
        Box(
            modifier = badgeModifier.background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = order.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    } else {
        // White outline on a light scrim stays visible on both bright and dark photos.
        Box(
            modifier = badgeModifier
                .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                .border(DailyTheme.sizes.selectionBorder, Color.White, CircleShape),
        )
    }
}