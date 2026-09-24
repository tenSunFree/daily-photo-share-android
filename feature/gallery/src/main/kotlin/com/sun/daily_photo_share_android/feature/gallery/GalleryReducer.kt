package com.sun.daily_photo_share_android.feature.gallery

import com.sun.daily_photo_share_android.core.model.PhotoPermissionState

internal object GalleryReducer {

    fun reduce(state: GalleryUiState, mutation: GalleryMutation): GalleryUiState = when (mutation) {
        is GalleryMutation.PermissionChecked -> {
            // Full -> Full keeps the revision, so returning to the screen does not reload the grid.
            val readableSetMayHaveChanged = mutation.state != state.permissionState ||
                    mutation.state == PhotoPermissionState.Partial
            state.copy(
                permissionState = mutation.state,
                accessRevision = if (readableSetMayHaveChanged) {
                    state.accessRevision + 1
                } else {
                    state.accessRevision
                },
            )
        }
    }
}