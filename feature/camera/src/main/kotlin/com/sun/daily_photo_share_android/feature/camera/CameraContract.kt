package com.sun.daily_photo_share_android.feature.camera

import com.sun.daily_photo_share_android.core.model.CaptureTarget

enum class CameraPermissionState { Granted, Requestable, NeedsSettings }

enum class CameraRuntimeState { Idle, Starting, Ready, Unavailable }

data class CameraUiState(
    val permissionState: CameraPermissionState? = null,
    val cameraState: CameraRuntimeState = CameraRuntimeState.Idle,
    val isCapturing: Boolean = false,
    val savedCount: Int = 0,
) {
    val canCapture: Boolean
        get() = permissionState == CameraPermissionState.Granted &&
                cameraState == CameraRuntimeState.Ready &&
                !isCapturing
}

sealed interface CameraIntent {
    data class PermissionObserved(
        val granted: Boolean,
        val shouldShowRationale: Boolean,
    ) : CameraIntent

    data object RequestPermissionClicked : CameraIntent
    data object OpenSettingsClicked : CameraIntent

    data object CameraStarting : CameraIntent
    data object CameraReady : CameraIntent
    data object CameraUnavailable : CameraIntent

    data object CaptureClicked : CameraIntent
    data object CaptureSucceeded : CameraIntent
    data object CaptureFailed : CameraIntent
}

sealed interface CameraEffect {
    data object RequestPermission : CameraEffect
    data object OpenSettings : CameraEffect
    data class CapturePhoto(val target: CaptureTarget) : CameraEffect
    data object ShowCaptureFailed : CameraEffect
}

internal sealed interface CameraMutation {
    data class PermissionChecked(val state: CameraPermissionState) : CameraMutation
    data object CameraStarting : CameraMutation
    data object CameraReady : CameraMutation
    data object CameraUnavailable : CameraMutation
    data object CaptureStarted : CameraMutation
    data object CaptureSucceeded : CameraMutation
    data object CaptureFailed : CameraMutation
}