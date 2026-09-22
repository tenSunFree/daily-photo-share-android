package com.sun.daily_photo_share_android.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Icons used by the app. Features reference these instead of the icon library,
 * so the icon source can change in one place.
 */
object DailyIcons {
    val Today: ImageVector get() = Icons.Filled.Today
    val Camera: ImageVector get() = Icons.Filled.CameraAlt
    val Gallery: ImageVector get() = Icons.Filled.PhotoLibrary
    val Share: ImageVector get() = Icons.Filled.Share
}