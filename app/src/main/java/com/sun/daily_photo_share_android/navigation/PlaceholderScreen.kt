package com.sun.daily_photo_share_android.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme

/** Stand-in for screens whose features are not implemented. */
@Composable
internal fun PlaceholderScreen(
    title: String,
    modifier: Modifier = Modifier,
    detail: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DailyTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(DailyTheme.spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        if (detail != null) {
            Text(text = detail, style = MaterialTheme.typography.bodyMedium)
        }
    }
}