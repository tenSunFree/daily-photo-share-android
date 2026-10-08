package com.sun.daily_photo_share_android.feature.gallery

import com.sun.daily_photo_share_android.core.model.MediaUri
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import com.sun.daily_photo_share_android.core.model.PhotoSelection
import com.sun.daily_photo_share_android.core.model.ShareRequest

data class GalleryUiState(
    /** null until the first check, so the screen never flashes a wrong permission state. */
    val permissionState: PhotoPermissionState? = null,
    /**
     * Bumped whenever the set of readable photos may have changed. With partial access the state
     * can stay Partial while the selected photos change, so the photo query must restart anyway.
     */
    val accessRevision: Int = 0,
    /** Ordered selection: list order is the share order and the badge number. */
    val selection: PhotoSelection = PhotoSelection(),
    /** True when the screen was opened to pick photos (Today "select photos for LINE"). */
    val launchedForSelection: Boolean = false,
    /** True while the selected photos are being checked before a share; blocks a second share. */
    val isPreparingShare: Boolean = false,
    /** A share that could not go ahead; shown as a dialog, so it survives rotation. */
    val shareProblem: ShareProblem? = null,
) {
    /** Derived, never stored, so "selecting" and "has a selection" cannot disagree. */
    val isSelectionMode: Boolean
        get() = launchedForSelection || !selection.isEmpty

    val canShare: Boolean
        get() = !selection.isEmpty && !isPreparingShare
}

sealed interface ShareProblem {
    /** Every selected photo is unreadable (deleted, or removed from partial access). */
    data object NothingReadable : ShareProblem

    /** LINE is missing or disabled; [request] can still go to the system share sheet. */
    data class LineNotInstalled(val request: ShareRequest) : ShareProblem
}

sealed interface GalleryIntent {
    /** Sent on every resume: picks up changes made in system Settings or in the photo selection. */
    data object CheckPermission : GalleryIntent
    data object RequestPermissionClicked : GalleryIntent
    data object ManageSelectedPhotosClicked : GalleryIntent
    data object OpenSettingsClicked : GalleryIntent

    data class PhotoTapped(val uri: MediaUri) : GalleryIntent
    data class PhotoLongPressed(val uri: MediaUri) : GalleryIntent
    data object ClearSelectionClicked : GalleryIntent

    /** Sent when the screen is opened for picking; safe to send more than once. */
    data object SelectionModeRequested : GalleryIntent

    data object ShareToLineClicked : GalleryIntent
    data object ShareClicked : GalleryIntent

    /** Reported by the Route when the share target could not be started. */
    data class ShareTargetUnavailable(val request: ShareRequest) : GalleryIntent
    data object UseSystemShareSheetClicked : GalleryIntent
    data object ShareProblemDismissed : GalleryIntent
}

/** One-off events. Delivered once, never kept in [GalleryUiState]. */
sealed interface GalleryEffect {
    data class RequestPermissions(val permissions: List<String>) : GalleryEffect
    data object OpenAppSettings : GalleryEffect

    /** [skippedCount] photos were unreadable and left out of [request]. */
    data class LaunchShare(val request: ShareRequest, val skippedCount: Int) : GalleryEffect
}

internal sealed interface GalleryMutation {
    data class PermissionChecked(val state: PhotoPermissionState) : GalleryMutation
    data class PhotoTapped(val uri: MediaUri) : GalleryMutation
    data class PhotoLongPressed(val uri: MediaUri) : GalleryMutation
    data object SelectionCleared : GalleryMutation
    data object SelectionModeRequested : GalleryMutation
    data class SelectionRestored(val uris: List<MediaUri>) : GalleryMutation
    data object SharePreparationStarted : GalleryMutation
    data class SharePreparationFinished(
        val unreadable: List<MediaUri>,
        val problem: ShareProblem?,
    ) : GalleryMutation

    data class ShareProblemShown(val problem: ShareProblem) : GalleryMutation
    data object ShareProblemDismissed : GalleryMutation
}