package com.sun.daily_photo_share_android.feature.gallery

import com.sun.daily_photo_share_android.core.model.PhotoPermissionState

data class GalleryUiState(
    /** null until the first check, so the screen never flashes a wrong permission state. */
    val permissionState: PhotoPermissionState? = null,
    /**
     * Bumped whenever the set of readable photos may have changed. With partial access the state
     * can stay Partial while the selected photos change, so the photo query must restart anyway.
     */
    val accessRevision: Int = 0,
)

sealed interface GalleryIntent {
    /** Sent on every resume: picks up changes made in system Settings or in the photo selection. */
    data object CheckPermission : GalleryIntent
    data object RequestPermissionClicked : GalleryIntent
    data object ManageSelectedPhotosClicked : GalleryIntent
    data object OpenSettingsClicked : GalleryIntent
}

/** One-off events. Delivered once, never kept in [GalleryUiState]. */
sealed interface GalleryEffect {
    data class RequestPermissions(val permissions: List<String>) : GalleryEffect
    data object OpenAppSettings : GalleryEffect
}

internal sealed interface GalleryMutation {
    data class PermissionChecked(val state: PhotoPermissionState) : GalleryMutation
}