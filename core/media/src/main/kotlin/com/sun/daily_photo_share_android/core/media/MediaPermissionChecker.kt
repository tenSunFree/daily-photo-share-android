package com.sun.daily_photo_share_android.core.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.sun.daily_photo_share_android.core.model.PhotoPermissionState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Android 10-12: READ_EXTERNAL_STORAGE. Android 13: READ_MEDIA_IMAGES.
 * Android 14+: READ_MEDIA_IMAGES (full) or READ_MEDIA_VISUAL_USER_SELECTED (partial);
 * requesting both shows the system "Allow all / Select photos / Don't allow" dialog.
 */
internal class MediaPermissionChecker @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun currentState(): PhotoPermissionState {
        val sdkInt = Build.VERSION.SDK_INT
        val hasFullAccess = hasPermission(fullAccessPermission(sdkInt))
        val hasPartialAccess = sdkInt >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                hasPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        return MediaPermissionResolver.resolve(sdkInt, hasFullAccess, hasPartialAccess)
    }

    fun permissionsToRequest(): List<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        )

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            listOf(Manifest.permission.READ_MEDIA_IMAGES)

        else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun fullAccessPermission(sdkInt: Int): String =
        if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}