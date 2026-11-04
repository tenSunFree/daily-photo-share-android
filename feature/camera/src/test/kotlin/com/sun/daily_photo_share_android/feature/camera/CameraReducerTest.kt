package com.sun.daily_photo_share_android.feature.camera

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CameraReducerTest {

    private val ready = CameraUiState(
        permissionState = CameraPermissionState.Granted,
        cameraState = CameraRuntimeState.Ready,
    )

    @Test
    fun captureSucceeded_clearsCapturingAndCountsThePhoto() {
        val result = CameraReducer.reduce(
            ready.copy(isCapturing = true, savedCount = 2),
            CameraMutation.CaptureSucceeded,
        )

        assertFalse(result.isCapturing)
        assertEquals(3, result.savedCount)
    }

    @Test
    fun captureFailed_clearsCapturingWithoutCounting() {
        val result = CameraReducer.reduce(
            ready.copy(isCapturing = true, savedCount = 2),
            CameraMutation.CaptureFailed,
        )

        assertFalse(result.isCapturing)
        assertEquals(2, result.savedCount)
    }

    @Test
    fun losingPermission_resetsTheCameraAndKeepsTheCount() {
        val result = CameraReducer.reduce(
            ready.copy(isCapturing = true, savedCount = 2),
            CameraMutation.PermissionChecked(CameraPermissionState.NeedsSettings),
        )

        assertEquals(CameraRuntimeState.Idle, result.cameraState)
        assertFalse(result.isCapturing)
        assertEquals(2, result.savedCount)
    }

    @Test
    fun canCapture_requiresGrantedReadyAndIdle() {
        assertEquals(true, ready.canCapture)
        assertEquals(false, ready.copy(isCapturing = true).canCapture)
        assertEquals(false, ready.copy(cameraState = CameraRuntimeState.Starting).canCapture)
        assertEquals(
            false,
            ready.copy(permissionState = CameraPermissionState.Requestable).canCapture,
        )
    }
}