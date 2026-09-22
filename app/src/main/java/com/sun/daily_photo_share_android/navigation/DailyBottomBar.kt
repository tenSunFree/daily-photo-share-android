package com.sun.daily_photo_share_android.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import com.sun.daily_photo_share_android.R
import com.sun.daily_photo_share_android.core.designsystem.DailyIcons
import com.sun.daily_photo_share_android.feature.today.TodayDestination

/** Today | Camera | Gallery. Camera is an action, so it is never shown as selected. */
@Composable
internal fun DailyBottomBar(
    currentDestination: NavDestination?,
    onTodayClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentDestination.isOn<TodayDestination>(),
            onClick = onTodayClick,
            icon = { Icon(DailyIcons.Today, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_today)) },
        )
        NavigationBarItem(
            selected = false,
            onClick = onCameraClick,
            icon = { Icon(DailyIcons.Camera, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_camera)) },
        )
        NavigationBarItem(
            selected = currentDestination.isOn<GalleryDestination>(),
            onClick = onGalleryClick,
            icon = { Icon(DailyIcons.Gallery, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_gallery)) },
        )
    }
}