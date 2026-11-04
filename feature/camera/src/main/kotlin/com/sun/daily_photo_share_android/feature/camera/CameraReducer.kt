package com.sun.daily_photo_share_android.feature.camera

internal object CameraReducer {

    fun reduce(state: CameraUiState, mutation: CameraMutation): CameraUiState =
        when (mutation) {
            is CameraMutation.PermissionChecked ->
                if (mutation.state == CameraPermissionState.Granted) {
                    state.copy(permissionState = mutation.state)
                } else {
                    state.copy(
                        permissionState = mutation.state,
                        cameraState = CameraRuntimeState.Idle,
                        isCapturing = false,
                    )
                }

            CameraMutation.CameraStarting ->
                state.copy(cameraState = CameraRuntimeState.Starting)

            CameraMutation.CameraReady ->
                state.copy(cameraState = CameraRuntimeState.Ready)

            CameraMutation.CameraUnavailable ->
                state.copy(
                    cameraState = CameraRuntimeState.Unavailable,
                    isCapturing = false,
                )

            CameraMutation.CaptureStarted ->
                state.copy(isCapturing = true)

            CameraMutation.CaptureSucceeded ->
                state.copy(isCapturing = false, savedCount = state.savedCount + 1)

            CameraMutation.CaptureFailed ->
                state.copy(isCapturing = false)
        }
}