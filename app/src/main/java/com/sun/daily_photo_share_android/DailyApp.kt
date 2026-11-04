package com.sun.daily_photo_share_android

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sun.daily_photo_share_android.feature.camera.CameraDestination
import com.sun.daily_photo_share_android.feature.gallery.GalleryDestination
import com.sun.daily_photo_share_android.feature.today.TodayDestination
import com.sun.daily_photo_share_android.navigation.DailyBottomBar
import com.sun.daily_photo_share_android.navigation.DailyNavHost
import com.sun.daily_photo_share_android.navigation.isOn
import com.sun.daily_photo_share_android.navigation.navigateToTopLevel

/** Root composable: bottom navigation on the top-level screens, full screen elsewhere. */
@Composable
fun DailyApp() {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = currentDestination.isOn<TodayDestination>() ||
            currentDestination.isOn<GalleryDestination>()
    // The camera draws edge to edge and handles the system bar insets itself, so it must not
    // receive the Scaffold padding as well.
    val isCamera = currentDestination.isOn<CameraDestination>()
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                DailyBottomBar(
                    currentDestination = currentDestination,
                    onTodayClick = { navController.navigateToTopLevel(TodayDestination) },
                    onCameraClick = {
                        navController.navigate(CameraDestination) { launchSingleTop = true }
                    },
                    onGalleryClick = { navController.navigateToTopLevel(GalleryDestination()) },
                )
            }
        },
    ) { innerPadding ->
        DailyNavHost(
            navController = navController,
            modifier = if (isCamera) Modifier else Modifier.padding(innerPadding),
        )
    }
}