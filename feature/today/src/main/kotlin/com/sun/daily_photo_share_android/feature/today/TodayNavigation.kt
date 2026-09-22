package com.sun.daily_photo_share_android.feature.today

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/** Type-safe navigation key of the Today screen. */
@Serializable
data object TodayDestination

fun NavGraphBuilder.todayScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallerySelection: () -> Unit,
) {
    composable<TodayDestination> {
        TodayRoute(
            onNavigateToCamera = onNavigateToCamera,
            onNavigateToGallerySelection = onNavigateToGallerySelection,
        )
    }
}