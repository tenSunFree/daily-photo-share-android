package com.sun.daily_photo_share_android.core.domain

import com.sun.daily_photo_share_android.core.model.PhotoPermissionState

interface PhotoPermissionRepository {
    /** Reads the OS-level grant; never requests anything. */
    fun currentState(): PhotoPermissionState

    /** Permission strings to request, chosen for the device's API level. */
    fun permissionsToRequest(): List<String>
}