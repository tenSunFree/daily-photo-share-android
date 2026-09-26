package com.sun.daily_photo_share_android.core.media

import android.os.Build
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaPermissionResolverTest {

    @Test
    fun fullAccess_isFull_onAnySdk() {
        assertEquals(
            PhotoPermissionState.Full,
            MediaPermissionResolver.resolve(
                sdkInt = 29,
                hasFullAccess = true,
                hasPartialAccess = false
            ),
        )
        assertEquals(
            PhotoPermissionState.Full,
            MediaPermissionResolver.resolve(
                sdkInt = 34,
                hasFullAccess = true,
                hasPartialAccess = true
            ),
        )
    }

    @Test
    fun partialAccess_isPartial_onlyOnApi34Plus() {
        assertEquals(
            PhotoPermissionState.Partial,
            MediaPermissionResolver.resolve(
                sdkInt = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
                hasFullAccess = false,
                hasPartialAccess = true,
            ),
        )
    }

    @Test
    fun partialAccess_onPreApi34_isTreatedAsDenied() {
        // READ_MEDIA_VISUAL_USER_SELECTED does not exist before API 34; a true flag there is a caller bug.
        assertEquals(
            PhotoPermissionState.Denied,
            MediaPermissionResolver.resolve(
                sdkInt = 33,
                hasFullAccess = false,
                hasPartialAccess = true
            ),
        )
    }

    @Test
    fun noAccess_isDenied() {
        assertEquals(
            PhotoPermissionState.Denied,
            MediaPermissionResolver.resolve(
                sdkInt = 34,
                hasFullAccess = false,
                hasPartialAccess = false
            ),
        )
    }
}