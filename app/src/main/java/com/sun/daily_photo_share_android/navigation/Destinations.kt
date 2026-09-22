package com.sun.daily_photo_share_android.navigation

import kotlinx.serialization.Serializable

/** Moves into the Gallery feature module together with the gallery implementation. */
@Serializable
data class GalleryDestination(val selectionMode: Boolean = false)

/** Moves into the Camera feature module together with the camera implementation. */
@Serializable
data object CameraDestination