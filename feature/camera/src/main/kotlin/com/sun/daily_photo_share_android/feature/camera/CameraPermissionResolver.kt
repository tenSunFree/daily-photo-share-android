package com.sun.daily_photo_share_android.feature.camera

internal object CameraPermissionResolver {

    fun resolve(
        granted: Boolean,
        shouldShowRationale: Boolean,
        hasRequestedBefore: Boolean,
    ): CameraPermissionState = when {
        granted -> CameraPermissionState.Granted
        !hasRequestedBefore -> CameraPermissionState.Requestable
        shouldShowRationale -> CameraPermissionState.Requestable
        else -> CameraPermissionState.NeedsSettings
    }
}