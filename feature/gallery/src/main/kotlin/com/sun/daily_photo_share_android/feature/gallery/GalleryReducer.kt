package com.sun.daily_photo_share_android.feature.gallery

import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import com.sun.daily_photo_share_android.core.model.PhotoSelection

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
                // Nothing is readable any more, so a kept selection could never be shared.
                selection = if (mutation.state == PhotoPermissionState.Denied) {
                    PhotoSelection()
                } else {
                    state.selection
                },
            )
        }
        // Outside selection mode a tap does nothing (it will open the full-screen preview).
        is GalleryMutation.PhotoTapped ->
            if (state.isSelectionMode) {
                state.copy(selection = state.selection.toggle(mutation.uri))
            } else {
                state
            }
        // Long press only ever adds, so pressing a selected photo again never removes it by accident.
        is GalleryMutation.PhotoLongPressed ->
            if (mutation.uri in state.selection) {
                state
            } else {
                state.copy(selection = state.selection.toggle(mutation.uri))
            }

        GalleryMutation.SelectionCleared -> state.copy(selection = state.selection.clear())
        GalleryMutation.SelectionModeRequested -> state.copy(launchedForSelection = true)
        is GalleryMutation.SelectionRestored ->
            state.copy(selection = PhotoSelection().addAll(mutation.uris))
    }
}