package com.sun.daily_photo_share_android.feature.gallery

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation key of the Gallery screen.
 * selectionMode is set by the Today "select photos for LINE" flow; the gallery screen
 * does not read it until multi-selection is implemented.
 */
@Serializable
data class GalleryDestination(val selectionMode: Boolean = false)

fun NavGraphBuilder.galleryScreen() {
    composable<GalleryDestination> {
        GalleryRoute()
    }
}