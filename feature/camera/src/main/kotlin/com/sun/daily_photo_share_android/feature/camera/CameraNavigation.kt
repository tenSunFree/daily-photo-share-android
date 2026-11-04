package com.sun.daily_photo_share_android.feature.camera

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object CameraDestination

fun NavGraphBuilder.cameraScreen(onBack: () -> Unit) {
    composable<CameraDestination> {
        CameraRoute(onBack = onBack)
    }
}