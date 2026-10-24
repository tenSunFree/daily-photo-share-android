package com.sun.daily_photo_share_android.feature.camera

import org.junit.Assert.assertEquals
import org.junit.Test

class CameraPermissionResolverTest {

    private fun resolve(granted: Boolean, rationale: Boolean, requested: Boolean) =
        CameraPermissionResolver.resolve(granted, rationale, requested)

    @Test
    fun granted_returnsGranted() {
        assertEquals(CameraPermissionState.Granted, resolve(true, false, false))
    }

    @Test
    fun neverRequested_isRequestable() {
        assertEquals(CameraPermissionState.Requestable, resolve(false, false, false))
    }

    @Test
    fun deniedButRationaleAvailable_isRequestable() {
        assertEquals(CameraPermissionState.Requestable, resolve(false, true, true))
    }

    @Test
    fun deniedWithoutRationaleAfterRequest_needsSettings() {
        assertEquals(CameraPermissionState.NeedsSettings, resolve(false, false, true))
    }
}