package com.sun.daily_photo_share_android.feature.gallery

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation key of the Gallery screen.
 * selectionMode = true opens the gallery ready to pick photos (Today "select photos for LINE").
 */
@Serializable
data class GalleryDestination(val selectionMode: Boolean = false)

fun NavGraphBuilder.galleryScreen() {
    composable<GalleryDestination> { entry ->
        val destination = entry.toRoute<GalleryDestination>()
        GalleryRoute(startInSelectionMode = destination.selectionMode)
    }
}