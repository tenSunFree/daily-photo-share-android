package com.sun.daily_photo_share_android.core.media

import android.os.Build
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState

/**
 * Pure permission-state decision. sdkInt is a parameter (not Build.VERSION.SDK_INT),
 * so every Android 10-14+ branch is covered by plain unit tests.
 */
internal object MediaPermissionResolver {
    fun resolve(
        sdkInt: Int,
        hasFullAccess: Boolean,
        hasPartialAccess: Boolean,
    ): PhotoPermissionState = when {
        hasFullAccess -> PhotoPermissionState.Full
        sdkInt >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && hasPartialAccess -> PhotoPermissionState.Partial
        else -> PhotoPermissionState.Denied
    }
}