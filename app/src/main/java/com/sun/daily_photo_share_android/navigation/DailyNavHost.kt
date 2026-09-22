package com.sun.daily_photo_share_android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sun.daily_photo_share_android.feature.today.TodayDestination
import com.sun.daily_photo_share_android.feature.today.todayScreen

@Composable
internal fun DailyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = TodayDestination,
        modifier = modifier,
    ) {
        todayScreen(
            onNavigateToCamera = {
                navController.navigate(CameraDestination) { launchSingleTop = true }
            },
            onNavigateToGallerySelection = {
                navController.navigate(GalleryDestination(selectionMode = true)) {
                    launchSingleTop = true
                }
            },
        )

        composable<GalleryDestination> { entry ->
            val destination = entry.toRoute<GalleryDestination>()
            PlaceholderScreen(
                title = "Gallery",
                detail = "selectionMode = ${destination.selectionMode}",
            )
        }

        composable<CameraDestination> {
            PlaceholderScreen(title = "Camera")
        }
    }
}